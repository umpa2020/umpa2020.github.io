package com.korea50k.RunShare.Activities.Racing

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import com.korea50k.RunShare.Activities.Profile.UserActivity
import com.korea50k.RunShare.Activities.RankFragment.RankDetailRecyclerViewAdapterMap
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.Util.map.ViewerMap
import kotlinx.android.synthetic.main.activity_racing_finish.*
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.activity_run_this_map.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class RacingFinishActivity : AppCompatActivity() {
    lateinit var racerData: RunningData
    lateinit var makerData: RunningData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing_finish)
        if (intent.getBooleanExtra("Result", false)) {
            resultRankTextView.text = "등수"
        } else {
            resultRankTextView.text = "실패"
        }
        racerData = intent.getSerializableExtra("Racer Data") as RunningData
        makerData = intent.getSerializableExtra("Maker Data") as RunningData
        makerDistanceTextView.text=String.format("%.3f km",makerData.distance/1000)
        makerLapTimeTextView.text=makerData.time
        makerMaxSpeedTextView.text=String.format("%.3f km/h",makerData.speed.max())
        makerAvgSpeedTextView.text=String.format("%.3f km/h",makerData.speed.average())

        racerLapTimeTextView.text=racerData.time
        racerMaxSpeedTextView.text=String.format("%.3f km/h",racerData.speed.max())
        racerAvgSpeedTextView.text=String.format("%.3f km/h",racerData.speed.average())
        //TODO:서버에서 해당 맵 랭크 받아오기

        Thread(Runnable {
            RetrofitClient.retrofitService.myRankingByMap(SharedPreValue.getNicknameData(this)!!,makerData.mapTitle)
                .enqueue(object :
                    retrofit2.Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        Log.d("server", "success to get rank")
                        Thread(Runnable {
                            try {
                                val url =
                                    URL("http://15.164.50.86/myRankingByMap.php?"+"Id="+SharedPreValue.getNicknameData(baseContext)+"&&MapTitle="+makerData.mapTitle)


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
                                val outStream = OutputStreamWriter(http.outputStream, "EUC-KR")
                                val writer = PrintWriter(outStream)
                                writer.write(buffer.toString())
                                writer.flush()
                                val tmp = InputStreamReader(http.inputStream, "EUC-KR")
                                val reader = BufferedReader(tmp)
                                val builder = StringBuilder()
                                var line: String? = reader.readLine()
                                while (line != null) {
                                    builder.append(line)
                                    line = reader.readLine()
                                }
                                Log.d("server", builder.toString())

                                /*var rankDetailMapDatas =
                                    ConvertJson.JsonToRankDetailMapDatas(builder.toString())
                                val mAdapter = RankDetailRecyclerViewAdapterMap(
                                    baseContext,
                                    rankDetailMapDatas
                                ) { rankDetailMapData ->
                                    //TODO Intent로 새로운 xml 열기
                                    val intent = Intent(baseContext, UserActivity::class.java)
                                    intent.putExtra("Id", rankDetailMapData.ChallengerId)
                                    startActivity(intent)

                                }
                                resultPlayerRankingRecycler!!.adapter = mAdapter
                                val lm = LinearLayoutManager(baseContext)
                                resultPlayerRankingRecycler!!.layoutManager = lm
                                resultPlayerRankingRecycler!!.setHasFixedSize(true)
*/
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

    fun onClick(view: View) {
        when (view.id) {
            R.id.OKButton -> {
                var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(newIntent)
            }
        }
    }
}
