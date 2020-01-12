package com.korea50k.RunShare.Activities.Running

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_running_save.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import android.graphics.Color
import android.os.AsyncTask
import android.os.Handler
import android.util.Base64
import android.widget.Toast
import androidx.core.net.toUri
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.Util.map.BasicMap
import com.korea50k.RunShare.Util.map.RunningMap
import com.korea50k.RunShare.Util.map.ViewerMap
import com.korea50k.RunShare.dataClass.Privacy
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RunningSaveActivity : AppCompatActivity() {
    lateinit var runningData: RunningData
    lateinit var map: ViewerMap
    var switch = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_running_save)

        runningData = intent.getSerializableExtra("Running Data") as RunningData

        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = ViewerMap(smf, this, runningData)
        distance_tv.text = String.format("%.3f km",runningData.distance/1000)
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

        time_tv.text = formatter.format(Date(runningData.time))

        speed_tv.text = String.format("%.3f km/h", runningData.speed.average())
        if (runningData.privacy == Privacy.PUBLIC) {
            racingRadio.isChecked = false
            racingRadio.isEnabled = false
            publicRadio.isChecked = true
        }
        setChart()
    }

    private fun setChart() {    //클래스로 따로 빼야할듯
        var lineChart = chart as LineChart

        val alts = ArrayList<Entry>()
        val speeds = ArrayList<Entry>()
        for (index in runningData.alts.indices) {
            alts.add(Entry(index.toFloat(), runningData.alts[index].toFloat()))
            speeds.add(Entry(index.toFloat(), runningData.speed[index].toFloat()))
        }

        val lineDataSet = LineDataSet(alts, "고도")
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.parseColor("#FF0000FF")
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.setCircleColor(Color.parseColor("#FFFFFFFF"))

        val lineDataSet2 = LineDataSet(speeds, "속력")
        lineDataSet2.lineWidth = 2f
        lineDataSet2.color = Color.parseColor("#FFFF0000")
        lineDataSet2.setDrawHorizontalHighlightIndicator(false)
        lineDataSet2.setDrawHighlightIndicators(false)
        lineDataSet2.setDrawValues(false)
        lineDataSet2.setCircleColor(Color.parseColor("#FFFFFFFF"))

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.data.addDataSet(lineDataSet2)

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.axisLeft
        yLAxis.textColor = Color.RED

        val yRAxis = lineChart.axisRight
        yRAxis.textColor = Color.BLUE
        yRAxis.axisMaximum = runningData.alts.max()!!.toFloat() + 5
        yRAxis.axisMinimum = runningData.alts.min()!!.toFloat() - 5
        //val description = Description()
        //description.text = ""

        lineChart.isDoubleTapToZoomEnabled = false;
        lineChart.setDrawGridBackground(false)
        //lineChart.description = description
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)
        lineChart.invalidate()
    }

    fun onClick(view: View) {
        if (switch == 0) {
            when (view.id) {
                R.id.save_btn -> {
                    map.CaptureMapScreen()
                    switch++
                }
            }
        }
    }

    fun save(imgPath: String) {
        //send runningData to server by json
        runningData.mapImage = imgPath
        runningData.mapTitle = mapTitleEdit.text.toString()
        runningData.mapExplanation = mapExplanationEdit.text.toString()
        runningData.id=SharedPreValue.getNicknameData(this)!!
        //runningData.mapTitle=runningData.mapTitle.replace(' ','|')
        when (privacyRadioGroup.checkedRadioButtonId) {
            R.id.racingRadio -> runningData.privacy = Privacy.RACING
            R.id.publicRadio -> runningData.privacy = Privacy.PUBLIC
            R.id.privateRadio -> runningData.privacy = Privacy.PRIVATE
        }

        var json = ConvertJson.RunningDataToJson(runningData)
        var bm = BitmapFactory.decodeFile(runningData.mapImage)
        var byteArrayOutputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        var byteArray = byteArrayOutputStream.toByteArray()
        var base64OfBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT)

        Log.wtf("WTF", json)
        Log.wtf("WTF",ConvertJson.RunningDataToJson(ConvertJson.JsonToRunningData(json)))
        class SaveTask : AsyncTask<Void, Void, String>() {
            override fun onPreExecute() {
                super.onPreExecute()
                //로딩화면띄우기
            }

            override fun doInBackground(vararg params: Void?): String? {

                try {
                    runningData(
                        runningData.id,
                        runningData.mapTitle,
                        runningData.mapExplanation,
                        json,
                        base64OfBitmap,
                        runningData.distance,
                        runningData.time,
                        0, 0, runningData.privacy
                    )
                } catch (e: java.lang.Exception) {
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //goToHome()

                //로딩화면 끄고 인텐트
            }
        }

        var saveTask = SaveTask()
        saveTask.execute()
    }

    fun goToHome() {
        var newIntent = Intent(this, MainActivity::class.java)
        newIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
        newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(newIntent)
    }

    private fun runningData(
        Id: String,
        MapTitle: String,
        MapExplanation: String,
        MapJson: String,
        MapImage: String,
        Distance: Double,
        Time: Long,
        Execute: Int,
        Likes: Int,
        Privacy: Privacy
    ) {
        RetrofitClient.retrofitService.runningDataUpoload(
            Id,
            MapTitle,
            MapExplanation,
            MapJson,
            MapImage,
            Distance,
            Time,
            Execute,
            Likes,
            Privacy
        ).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                try {
                    val result: String? = response.body().toString()
                    Toast.makeText(
                        this@RunningSaveActivity,
                        "json 업로드 성공" + result,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    goToHome()
                } catch (e: Exception) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@RunningSaveActivity, "서버 작업 실패", Toast.LENGTH_SHORT).show()
                Log.d("ssmm11", t.message);
                t.printStackTrace()
            }
        })
    }
}