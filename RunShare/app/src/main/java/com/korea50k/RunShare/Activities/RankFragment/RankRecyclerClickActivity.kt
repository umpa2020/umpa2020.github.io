package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.fragment_rank_map.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RankRecyclerClickActivity : AppCompatActivity() {
    var mJsonString = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)

        val intent =  getIntent()
        val MapTitle = intent.extras?.getString("MapTitle")
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

                    rankingMapDownload("kjb")
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
    private fun rankingMapDownload(Id: String) {
        RetrofitClient.retrofitService.rankingMapDownload(Id).enqueue(object :
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

                val mAdapter = RankDetailRecyclerViewAdapter_Map(baseContext, rankDetailMapDatas){ rankmapdata ->

                }
                rank_detailRecyclerView!!.adapter = mAdapter
                val lm = LinearLayoutManager(baseContext)
                rank_detailRecyclerView!!.layoutManager = lm
                rank_detailRecyclerView!!.setHasFixedSize(true)
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val serverURL = params[0]
            Log.d("ssmm11", "받아온 url = " +serverURL)
            try {
                val url = URL(serverURL)
                val httpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.setReadTimeout(5000)
                httpURLConnection.setConnectTimeout(5000)
                httpURLConnection.connect()

                val responseStatusCode = httpURLConnection.getResponseCode()
                Log.d("ssmm11", "response code - $responseStatusCode")

                val inputStream: InputStream
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream()
                } else {
                    inputStream = httpURLConnection.getErrorStream()
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)

                val sb = StringBuilder()

                var line : String? = ""
                while (line != null) {
                    line = bufferedReader.readLine()
                    sb.append(line)
                }

                bufferedReader.close()
                return sb.toString().trim { it <= ' ' }

            } catch (e: Exception) {
                Log.d("ssmm11", "InsertData: Error ", e)
                errorString = e.toString()
                return null
            }
        }
    }
}