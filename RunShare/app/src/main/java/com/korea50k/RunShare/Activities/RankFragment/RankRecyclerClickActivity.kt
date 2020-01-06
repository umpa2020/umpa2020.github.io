package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import com.korea50k.RunShare.Activities.MapDetailActivity
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.dataClass.ConvertJson
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RankRecyclerClickActivity : AppCompatActivity() {
    var count = false
    var mJsonString = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)

        rank_topplayer_optionButton.setOnClickListener{
            if(count)
                count = false
            else
                count = true

            if(count == true){
                rank_TopplayerChoice_linearlayout.visibility = View.VISIBLE
                rank_topplayer_optionButton.setBackgroundResource(R.drawable.ic_up_button)
            }
            if(count == false){
                rank_TopplayerChoice_linearlayout.visibility = View.GONE
                rank_topplayer_optionButton.setBackgroundResource(R.drawable.ic_down_button)
            }
        }

        if(intent.hasExtra("mapName")){
            mapName_TextView.text = intent.getStringExtra("mapName")
        }else{
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }


       /* //TODO:서버에서 데이터 가져와서 해야함

        val assetManager = resources.assets
        val inputStream= assetManager.open("datajson")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        var rankMapDatas = ConvertJson.JsonToRankMapDatas(jsonString)


        //리사이클러 뷰 클릭 리스너 부분
        val mAdapter = RankRecyclerViewAdapter_Map(this, rankMapDatas){ rankmapdata ->
            //TODO Intent로 새로운 xml 열기, 플레이어 프로필로
            Toast.makeText(this, "맵 이름 :  ${rankmapdata.MapTitle}, 실행 수 : ${rankmapdata.Excute}", Toast.LENGTH_SHORT).show()
        }

        rank_detailRecyclerView.adapter = mAdapter


        val lm = LinearLayoutManager(this)
        rank_detailRecyclerView.layoutManager = lm
        rank_detailRecyclerView.setHasFixedSize(true)*/

        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    rankDownload("kjb")
                    //TODO:피드에서 이미지 적용해볼 소스코드

                    /* val url =
                         URL("https://runsharetest.s3.ap-northeast-2.amazonaws.com/kjb/ImageTitle.png")
                     val conn = url.openConnection()
                     conn.connect()
                     val bis = BufferedInputStream(conn.getInputStream())
                     val bm = BitmapFactory.decodeStream(bis)
                     bis.close()*/
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
                //imageTest.setImageBitmap(bm)
            }
        }

        var Start : SaveTask = SaveTask()
        Start.execute()

        val task = GetData()
        task.execute("http://15.164.50.86/rankDownload.php")


    }


    fun onClick(v: View){
        var nextIntent= Intent(this,MapDetailActivity::class.java)
        startActivity(nextIntent)
    }

    private fun rankDownload(Id: String) {
        RetrofitClient.retrofitService.rankDownload(Id).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                    Toast.makeText(baseContext, "DB 다운로드 성공" + result,Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }

            override fun onFailure( call: Call<ResponseBody>,t: Throwable) {
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
                var rankMapDatas = ConvertJson.JsonToRankMapDatas(mJsonString)

               /* val mAdapter =
                    RankRecyclerViewAdapter_Map(activity!!, rankMapDatas) { rankmapdata ->
                        //TODO Intent로 새로운 xml 열기
                        val intent = Intent(context, RankRecyclerClickActivity::class.java)
                        startActivity(intent)
                    }
                view?.rank_recycler_map!!.adapter = mAdapter
                val lm = LinearLayoutManager(context)
                view?.rank_recycler_map!!.layoutManager = lm
                view?.rank_recycler_map!!.setHasFixedSize(true)*/

                //리사이클러 뷰 클릭 리스너 부분
                val mAdapter = RankRecyclerViewAdapter_Map(baseContext, rankMapDatas){ rankmapdata ->
                    //TODO Intent로 새로운 xml 열기, 플레이어 프로필로
                    Toast.makeText(baseContext, "맵 이름 :  ${rankmapdata.MapTitle}, 실행 수 : ${rankmapdata.Excute}", Toast.LENGTH_SHORT).show()
                }

                rank_detailRecyclerView.adapter = mAdapter


                val lm = LinearLayoutManager(baseContext)
                rank_detailRecyclerView.layoutManager = lm
                rank_detailRecyclerView.setHasFixedSize(true)
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val serverURL = params[0]
            Log.d("ssmm11", "받아온 url = " + serverURL)
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

                var line: String? = ""
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
