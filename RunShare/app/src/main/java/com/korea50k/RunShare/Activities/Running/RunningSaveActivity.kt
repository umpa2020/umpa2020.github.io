package com.korea50k.RunShare.Activities.Running

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.korea50k.RunShare.dataClass.ConvertJson
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
import android.util.Base64
import android.widget.Toast
import androidx.core.net.toUri
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.korea50k.RunShare.Activities.MainActivity
import com.korea50k.RunShare.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL


class RunningSaveActivity : AppCompatActivity() {
    lateinit var runningData:RunningData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_save)

        runningData=intent.getSerializableExtra("Running Data") as RunningData
        map_img.setImageURI(runningData.bitmap.toUri())
        distance_tv.text=runningData.distance.toString()
        time_tv.text=runningData.time
        speed_tv.text=runningData.speed.toString()
        calorie_tv.text=runningData.cal.toString()
        setChart()
    }

    private fun setChart() {    //클래스로 따로 빼야할듯
        var lineChart = chart as LineChart

        val entries = ArrayList<Entry>()
        for(alts in runningData.alts.indices){
            entries.add(Entry(alts.toFloat(), runningData.alts[alts].toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "속성명1")
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.parseColor("#FFA1B4DC")
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.getAxisLeft()
        yLAxis.textColor = Color.BLACK

        val yRAxis = lineChart.getAxisRight()
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val description = Description()
        description.text = ""

        lineChart.isDoubleTapToZoomEnabled = false;
        lineChart.setDrawGridBackground(false)
        lineChart.description = description
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)
        lineChart.invalidate()
    }

    fun onClick(view: View) {
        when (view.id) {
            com.korea50k.RunShare.R.id.save_btn -> {
                //send runningData to server by json
                runningData.map_title=save_title_edit.text.toString()

                var json = ConvertJson.RunningDataToJson(runningData)
                //send to server

                var bm = BitmapFactory.decodeFile(runningData.bitmap)

                var byteArrayOutputStream = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                var byteArray = byteArrayOutputStream.toByteArray()
                var base64OfBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT)

                var imageTest = testImage

                class SaveTask : AsyncTask<Void, Void, String>(){
                    override fun onPreExecute() {
                        super.onPreExecute()
                    }

                    override fun doInBackground(vararg params: Void?): String? {
                        try {
                            s3Upload("kjb", runningData.map_title, "racingMap description", json, base64OfBitmap, "100kcal","100km",
                                "14km/h","00:12:11",0,0,1)

                            //JsonUpload("kjb", "test", 11)

                            val url =
                                URL("https://runsharetest.s3.ap-northeast-2.amazonaws.com/kjb/ImageTitle.png")
                            val conn = url.openConnection()
                            conn.connect()
                            val bis = BufferedInputStream(conn.getInputStream())
                            val bm = BitmapFactory.decodeStream(bis)
                            bis.close()
                        } catch (e : java.lang.Exception) {
                            Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                        }
                        return null
                    }

                    override fun onPostExecute(result: String?) {
                        super.onPostExecute(result)
                        imageTest.setImageBitmap(bm)
                    }
                }

                var Start : SaveTask = SaveTask()
                Start.execute()

                /*
                var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags= FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)

                startActivity(newIntent)

                 */
            }
        }
    }

    private fun s3Upload(Id : String, MapTitle : String, MapDescription : String, MapJson : String, MapImage : String, Kcal : String, Distance : String,
                         Velocity : String, Time : String, Excute : Int, Likes : Int, Status: Int) {
        RetrofitClient.retrofitService.s3Upload(Id, MapTitle, MapDescription, MapJson, MapImage, Kcal, Distance, Velocity, Time, Excute, Likes, Status).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                    Toast.makeText(this@RunningSaveActivity, "json 업로드 성공" + result, Toast.LENGTH_SHORT).show()
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
    private fun dbDownloadtest(Id: String) {
        RetrofitClient.retrofitService.dbDownloadtest(Id).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                    Log.d("ssmm11", "DB DOWNLOAD 성공 result = " + response.body())

                    Toast.makeText(this@RunningSaveActivity, "DB DOWNLOAD 성공" + result, Toast.LENGTH_SHORT).show()
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
