package com.korea50k.RunShare.Activities.FeedFragment


import android.content.Intent
import android.content.res.AssetManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.Activities.RankFragment.RankRecyclerClickActivity
import com.korea50k.RunShare.Activities.RankFragment.RankRecyclerViewAdapter_Map

import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.dataClass.FeedMapData
import com.korea50k.RunShare.dataClass.RankMapData
import kotlinx.android.synthetic.main.fragment_feed_map_nocomment.view.*
import kotlinx.android.synthetic.main.fragment_rank_map.view.*
import kotlinx.android.synthetic.main.recycler_feed_map.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class fragment_feed_map : Fragment() {
    var mJsonString = "";
    var list = arrayListOf<FeedMapData>() // 그대로 냅둬야 할것
    var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(R.layout.recycler_feed_map, container, false)
        val view2: View = inflater!!.inflate(R.layout.fragment_feed_map_nocomment, container, false)

        val heartchange = view2.findViewById<View>(com.korea50k.RunShare.R.id.detailviewitem_favorite_imageview) as ImageView
        heartchange.setOnClickListener{
            count++//누를때마다 증가

            if(count % 2 == 1){
                heartchange.setBackgroundResource(R.drawable.ic_favorite)
            }
            if(count % 2 == 0){
                heartchange.setBackgroundResource(R.drawable.ic_favorite_border)
            }
        }

        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    feedDownload("kjb")
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
            }
        }

        var Start : SaveTask = SaveTask()
        Start.execute()

        val task = GetData()
        task.execute("http://15.164.50.86/feedDownload.php")

        return view
    }

    private fun feedDownload(Id: String) {
        RetrofitClient.retrofitService.feedDownload(Id).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                    //Toast.makeText(context, "DB 다운로드 성공" + result,Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }

            override fun onFailure( call: Call<ResponseBody>,t: Throwable) {
                //Toast.makeText(context, "서버 작업 실패", Toast.LENGTH_SHORT).show()
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
                var feedMapDatas = ConvertJson.JsonToFeedMapDatas(mJsonString)

                val mAdapter = FeedRecyclerViewAdapter_Map(activity!!, feedMapDatas){ feedmapdata ->
                    //TODO Intent로 새로운 xml 열기
                  /*  val intent = Intent(context, RankRecyclerClickActivity::class.java)
                    intent.putExtra("MapTitle", rankmapdata.MapTitle)
                    intent.putExtra("MapImage", rankmapdata.MapImage)
                    startActivity(intent)*/
                }
                view?.feed_recycler_map!!.adapter = mAdapter
                val lm = LinearLayoutManager(context)
                view?.feed_recycler_map!!.layoutManager = lm
                view?.feed_recycler_map!!.setHasFixedSize(true)
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
