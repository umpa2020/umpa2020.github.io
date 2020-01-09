package com.korea50k.RunShare.Activities.RankFragment


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.Activities.Profile.UserActivity

import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import kotlinx.android.synthetic.main.fragment_rank_map.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class fragment_rank_map : Fragment() {
    var mJsonString = "";

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(R.layout.fragment_rank_map, container, false)

        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    rankDownload("kjb")

                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "랭크 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
            }
        }

        var Start : SaveTask = SaveTask()
        Start.execute()

        val task = GetData()
        task.execute("http://15.164.50.86/rankDownload.php")

        return view
    }

    private fun rankDownload(Id: String) {
        RetrofitClient.retrofitService.rankDownload(Id).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                    Toast.makeText(context, "DB 다운로드 성공" + result,Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }

            override fun onFailure( call: Call<ResponseBody>,t: Throwable) {
                Toast.makeText(context, "서버 작업 실패", Toast.LENGTH_SHORT).show()
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

                val mAdapter = RankRecyclerViewAdapter_Map(activity!!, rankMapDatas){ rankmapdata ->


                    //TODO Intent로 새로운 xml 열기

                    val intent = Intent(context, RankRecyclerClickActivity::class.java)
                    intent.putExtra("MapTitle", rankmapdata.mapTitle)
                    intent.putExtra("MapImage", rankmapdata.mapImage)
                    intent.putExtra("Id", rankmapdata.id)
                    startActivity(intent)
                    
                }

                view?.rank_recycler_map!!.adapter = mAdapter
                val lm = LinearLayoutManager(context)
                view?.rank_recycler_map!!.layoutManager = lm
                view?.rank_recycler_map!!.setHasFixedSize(true)
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
