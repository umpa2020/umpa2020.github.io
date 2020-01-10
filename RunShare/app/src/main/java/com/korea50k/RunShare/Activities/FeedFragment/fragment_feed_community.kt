package com.korea50k.RunShare.Activities.FeedFragment


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager

import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.dataClass.FeedCommunityData
import kotlinx.android.synthetic.main.recycler_feed_community.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class fragment_feed_community : Fragment() {
    var mJsonString = "";
    var list = arrayListOf<FeedCommunityData>() // 그대로 냅둬야 할것
    var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(R.layout.recycler_feed_community, container, false)
        val view2: View = inflater!!.inflate(R.layout.fragment_feed_community_nocomment, container, false)
        val view3: View = inflater!!.inflate(R.layout.activity_feed_community_comment, container, false)

        val heartchange = view2.findViewById<View>(com.korea50k.RunShare.R.id.detailviewitem_favorite_imageview) as ImageView


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
                } catch (e: Exception) {

                }
            }

            override fun onFailure( call: Call<ResponseBody>,t: Throwable) {
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

        /*override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result == null) {
            } else {
                mJsonString = result
                var feedCommunityData = ConvertJson.JsonToFeedCommunityDatas(mJsonString)

                val mAdapter = FeedRecyclerViewAdapter_Community(activity!!, feedCommunityDatas)
                mAdapter.itemClick = object: FeedRecyclerViewAdapter_Community.ItemClick {
                    override fun onClick(view: View, position: Int) {
                        Log.d("ssmm11", position.toString())
                        val intent = Intent(context, FeedRecyclerClickActivity_Commnuity::class.java)
                        startActivity(intent)
                    }
                }
                view?.feed_recycler_community!!.adapter = mAdapter
                val lm = LinearLayoutManager(context)
                view?.feed_recycler_community!!.layoutManager = lm
                view?.feed_recycler_community!!.setHasFixedSize(true)
            }
        }*/

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
