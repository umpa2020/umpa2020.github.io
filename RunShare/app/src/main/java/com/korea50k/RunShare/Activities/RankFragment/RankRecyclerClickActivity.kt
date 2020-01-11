package com.korea50k.RunShare.Activities.RankFragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.Activities.Profile.UserActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import kotlinx.android.synthetic.main.activity_racing_finish.*
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class RankRecyclerClickActivity : AppCompatActivity() {
    var mJsonString = ""
    var MapTitle = ""

    //lateinit var mcontext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)

        //mcontext = baseContext
        val intent =  getIntent()
        MapTitle = intent.extras?.getString("MapTitle").toString()
        mapName_TextView.text=MapTitle

        class SetImageTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }
            var bm: Bitmap? = null

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    /*val url =
                        URL(MapImage)
                    val conn = url.openConnection()
                    conn.connect()
                    val bis = BufferedInputStream(conn.getInputStream())
                    bm = BitmapFactory.decodeStream(bis)
                    bis.close()*/

                    rankingMapDownload(MapTitle!!)
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
               // rank_root_preview.setImageBitmap(bm)
            }
        }
        var Start = SetImageTask()
        Start.execute()

      /*  Thread(Runnable {
            RetrofitClient.retrofitService.playerRankingAboutMapDownload(MapTitle)
                .enqueue(object :
                    retrofit2.Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        Log.d("server", "success to get makerData")
                        Thread(Runnable {
                            try {
                                val url =
                                    URL("http://15.164.50.86/playerRankingAboutMapDownload.php")
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
                                buffer.append("MapTitle").append("=").append(MapTitle)

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
                                var rankDetailMapDatas =
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
        }).start()*/
    }
    private fun rankingMapDownload(MapTitle: String) {
        RetrofitClient.retrofitService.rankingMapDownload(MapTitle).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    Log.d("server","Players Ranking About Map")
                    val result: String? = response.body().toString()
                    Toast.makeText(baseContext, "DB 다운로드 성공" + result, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(baseContext, "서버 작업 실패", Toast.LENGTH_SHORT).show()
                Log.d("ssmm11", t.message);
                t.printStackTrace()
            }
        })
    }


    fun onClick(v: View){
        var newIntent = Intent(this, RunThisMapActivity::class.java)
        newIntent.putExtra("MapTitle",MapTitle)
        startActivity(newIntent)
    }
}