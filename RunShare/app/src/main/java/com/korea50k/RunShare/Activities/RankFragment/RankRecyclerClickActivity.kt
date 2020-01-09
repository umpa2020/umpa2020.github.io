package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)

        val intent =  getIntent()
        MapTitle = intent.extras?.getString("MapTitle").toString()
        val MapImage = intent.extras?.getString("MapImage")
        val Id = intent.extras?.getString("Id")
        mapName_TextView.setText(MapTitle)
        ID_TextView.setText(Id)


        class SetImageTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }
            lateinit var bm: Bitmap

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    val url =
                        URL(MapImage)
                    val conn = url.openConnection()
                    conn.connect()
                    val bis = BufferedInputStream(conn.getInputStream())
                    bm = BitmapFactory.decodeStream(bis)
                    bis.close()

                    rankingMapDownload(MapTitle!!)
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
                rank_root_preview.setImageBitmap(bm)
            }
        }
        var Start = SetImageTask()
        Start.execute()

        val task = GetData()
        task.execute("http://15.164.50.86/rankingMapDownload.php")
    }
    private fun rankingMapDownload(MapTitle: String) {
        RetrofitClient.retrofitService.rankingMapDownload(MapTitle).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
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

    private inner class GetData : AsyncTask<String, Void, String>() {
        internal var errorString: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result == null) {
            } else {
                mJsonString = result
                var rankDetailMapDatas = ConvertJson.JsonToRankDetailMapDatas(mJsonString)

                val mAdapter = RankDetailRecyclerViewAdapterMap(baseContext, rankDetailMapDatas){ rankDetailMapData ->
/*
                    //TODO Intent로 새로운 xml 열기
                    val intent = Intent(this@RankRecyclerClickActivity, RankRecyclerClickActivity::class.java)
                    intent.putExtra("Id", rankDetailMapData.id)
                    startActivity(intent)*/
                }
                rank_detailRecyclerView!!.adapter = mAdapter
                val lm = LinearLayoutManager(baseContext)
                rank_detailRecyclerView!!.layoutManager = lm
                rank_detailRecyclerView!!.setHasFixedSize(true)
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val serverURL = params[0]
            Log.d("ssmm11", "받아온 url = " + serverURL)

            try {
                val url = URL(params[0])
                val http = url.openConnection() as HttpURLConnection
                http.defaultUseCaches = false
                http.doInput = true
                http.doOutput = true
                http.requestMethod = "POST"

                http.setRequestProperty("content-type", "application/x-www-form-urlencoded")
                val buffer = StringBuffer()
                buffer.append("MapTitle").append("=").append(MapTitle)


                val outStream = OutputStreamWriter(http.outputStream, "EUC-KR")
                val writer = PrintWriter(outStream)
                writer.write(buffer.toString())
                writer.flush()
                val tmp = InputStreamReader(http.inputStream, "EUC-KR")
                val reader = BufferedReader(tmp)
                val builder = StringBuilder()
                var line: String? = ""
                while (line != null) {
                    line = reader.readLine()
                    builder.append(line)
                }
                return builder.toString()
            } catch (e: MalformedURLException) {
                return ""
            }

        }
    }
    fun onClick(v: View){
        var newIntent = Intent(this, RunThisMapActivity::class.java)
        startActivity(newIntent)
    }
}