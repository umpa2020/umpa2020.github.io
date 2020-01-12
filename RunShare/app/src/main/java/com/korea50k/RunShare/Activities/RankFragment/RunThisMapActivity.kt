package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.Gson
import com.korea50k.RunShare.Activities.Racing.RacingActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.Util.map.ViewerMap
import com.korea50k.RunShare.dataClass.RunningData
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.activity_run_this_map.*
import kotlinx.android.synthetic.main.activity_running.view.*
import kotlinx.android.synthetic.main.activity_running_save.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RunThisMapActivity : AppCompatActivity() {
    val WSY = "WSY"

    lateinit var mapTitle: String
    lateinit var jsonString: String
    lateinit var makerData: RunningData
    lateinit var activity: RunThisMapActivity
    lateinit var map: ViewerMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_run_this_map)
        mapTitle = intent.getStringExtra("MapTitle")
        Log.d("mapTitle",mapTitle)
        activity = this
        val smf = supportFragmentManager.findFragmentById(R.id.runThisMapViewer) as SupportMapFragment

        Thread(Runnable {
            RetrofitClient.retrofitService.runningDataDownload(mapTitle).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    Log.d("server", "success to get makerData")
                    Thread(Runnable {
                        try {
                            val url = URL("http://15.164.50.86/runningDataDownload.php")
                            val http = url.openConnection() as HttpURLConnection
                            http.defaultUseCaches = false
                            http.doInput = true
                            http.doOutput = true
                            http.requestMethod = "POST"

                            http.setRequestProperty(
                                "content-type",
                                "application/x-www-form-urlencoded"
                            )
                            val buffer = StringBuffer()
                            buffer.append("MapTitle").append("=").append(mapTitle)


                            val outStream = OutputStreamWriter(http.outputStream, "UTF8")
                            val writer = PrintWriter(outStream)
                            writer.write(buffer.toString())
                            writer.flush()
                            val tmp = InputStreamReader(http.inputStream, "UTF8")
                            val reader = BufferedReader(tmp)
                            val builder = StringBuilder()
                            var line: String? = reader.readLine()
                            while (line != null) {
                                builder.append(line)
                                line = reader.readLine()
                            }
                           // Log.d("server", String(builder.toString().toByteArray(),Charsets.UTF_8))
                            Log.d(WSY,builder.toString())
                            makerData = ConvertJson.JsonToRunningData(builder.toString())
                            Log.d(WSY, makerData.toString())
                            activity.runOnUiThread(Runnable {
                                map = ViewerMap(smf, activity, makerData)
                                activity.runThisMapTitle.text = makerData.mapTitle.replace('|',' ')
                                activity.runThisMapExplanation.text=makerData.mapExplanation
                                activity.runThisMapId.text = makerData.id
                                activity.runThisMapDistance.text=String.format("%.3f",makerData.distance/1000)
                                val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
                                formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
                                activity.runThisMapTime.text=formatter.format(Date(makerData.time))
                                activity.runThisMapSpeed.text=String.format("%.3f",makerData.speed.average())
                                setChart()
//TODO:Save할때 id도 json에 넣기

                            })
                        } catch (e: MalformedURLException) {
                            Log.e("server", e.toString())
                        }
                    }).start()


                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("server", t.message);
                    t.printStackTrace()
                }
            })
        }).start()
    }

    fun onClick(v: View) {

        var newIntent = Intent(this, RacingActivity::class.java)
        newIntent.putExtra("MakerData", makerData)
        startActivity(newIntent)
    }

    private fun setChart() {    //클래스로 따로 빼야할듯
        var lineChart = runThisMapChart as CombinedChart
        val alts = ArrayList<BarEntry>()
        val speeds = ArrayList<Entry>()

        for (index in makerData.alts.indices) {
            alts.add(BarEntry(index.toFloat(), makerData.alts[index].toFloat()))
            speeds.add(Entry(index.toFloat(), makerData.speed[index].toFloat()))
        }

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.axisLeft
        yLAxis.textColor = Color.RED
        yLAxis.axisMaximum = makerData.speed.max()!!.toFloat() + 5
        yLAxis.axisMinimum = 0F

        val yRAxis = lineChart.axisRight
        yRAxis.textColor = Color.BLUE
        yRAxis.axisMaximum = makerData.alts.max()!!.toFloat() + 5
        yRAxis.axisMinimum = makerData.alts.min()!!.toFloat() - 5

        Log.d(WSY, "고도 : " + alts.size.toString())
        Log.d(WSY, "속도 : " + speeds.size.toString())


        // 고도 셋팅
        var altsData = LineData()

//        val lineDataSet = LineDataSet(alts, "고도")
//        lineDataSet.lineWidth = 1.5f
//        lineDataSet.color = Color.parseColor("#FF0000FF") // 파랑
//        lineDataSet.setDrawHorizontalHighlightIndicator(false)
//        lineDataSet.setDrawHighlightIndicators(false)
//        lineDataSet.setDrawValues(false)
//       // lineDataSet.setCircleColor(Color.parseColor("#FFFFFFFF"))
//        lineDataSet.setDrawCircles(false)
//        lineDataSet.setDrawCircleHole(false)
//        lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        val set =  BarDataSet(alts, "고도")
        set.color = Color.parseColor("#FF0000FF") // 파랑
        set.barBorderColor = Color.parseColor("#FF0000FF") // 파랑
        set.barBorderWidth =  3f
        set.setDrawValues(false)
        set.setDrawIcons(false)
        set.isHighlightEnabled = false
        set.axisDependency = YAxis.AxisDependency.RIGHT

        //altsData.addDataSet(set)

        // 스피드 셋팅
        var speedsData = LineData()
        val lineDataSet2 = LineDataSet(speeds, "속력")
        lineDataSet2.lineWidth = 1.5f
        lineDataSet2.color = Color.parseColor("#FFFF0000") // 빨강
        lineDataSet2.setDrawHorizontalHighlightIndicator(false)
        lineDataSet2.setDrawHighlightIndicators(false)
        lineDataSet2.setDrawValues(false)
//        lineDataSet2.setCircleColor(Color.parseColor("#FFFF0000"))
//        lineDataSet2.setCircleColorHole(Color.parseColor("#FFFF0000"))
        lineDataSet2.setDrawCircles(false)
        lineDataSet2.setDrawCircleHole(false)
        lineDataSet2.axisDependency = YAxis.AxisDependency.LEFT

        speedsData.addDataSet(lineDataSet2)

        var combinedData = CombinedData()

        combinedData.setData(BarData(set))
        combinedData.setData(speedsData)

//        val lineData = LineData(lineDataSet)
//        lineChart.data = lineData
//        lineChart.data.addDataSet(lineDataSet2)


        //val description = Description()
        //description.text = ""

        lineChart.isDoubleTapToZoomEnabled = false // 더블 클릭 막기
        lineChart.setDrawGridBackground(false)
        //lineChart.description = description
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)

        lineChart.data = combinedData
        lineChart.invalidate()
    }

}

