package com.korea50k.RunShare.Activities.FeedFragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.dataClass.FeedMapCommentData
import com.korea50k.RunShare.dataClass.FeedMapData
import kotlinx.android.synthetic.main.activity_feed_map_comment.*
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.comment_feed_item.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class FeedRecyclerClickActivityMap : AppCompatActivity() {
    var mJsonString = ""
    lateinit var feedMapDatas : ArrayList<FeedMapData>

    var Comment = ArrayList<FeedMapCommentData>()
    var mAdapter_Map = FeedRecyclerMapComment(this, Comment)

    var MapTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_map_comment)

        val intent =  getIntent()
        MapTitle = intent.extras?.getString("MapTitle").toString()
        Log.d("ssmm11", "받은  맵타이틀 = " +MapTitle)

        /*  val Id = intent.extras?.getString("Id")
        val Heart = intent.extras?.getString("Heart")
        val Likes = intent.extras?.getString("Likes")
        val MapImage = intent.extras?.getString("MapImage")*/

        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    feedDownloadWithMapTitle(MapTitle!!)

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
        task.execute("http://15.164.50.86/feedDownloadWithMapTitle.php?MapTitle="+MapTitle)

        feed_map_recycler_comment!!.adapter = mAdapter_Map
        val lm = LinearLayoutManager(this)
        feed_map_recycler_comment!!.layoutManager = lm
        feed_map_recycler_comment!!.setHasFixedSize(true)

        feed_map_comment_button.setOnClickListener(View.OnClickListener {

            feed_map_comment_editmessage.text.toString()
            var feedMapCommentData = FeedMapCommentData()
            feedMapCommentData.MapComment = feed_map_comment_editmessage.text.toString()
            feedMapCommentData.UserId = "kjb"
            //feedMapCommentData.UserImage =

            Comment.add(feedMapCommentData)
            mAdapter_Map.notifyDataSetChanged()

            feed_map_comment_editmessage.setText("")
        })
    }

    private fun feedDownloadWithMapTitle(MapTitle: String) {
        RetrofitClient.retrofitService.feedDownloadWithMapTitle(MapTitle).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                } catch (e: Exception) {

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("ssmm11", t.message);
                t.printStackTrace()
            }
        })
    }

    private inner class GetData() : AsyncTask<String, Void, String>() {
        internal var errorString: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result == null) {
            } else {
                mJsonString = result
                feedMapDatas = ConvertJson.JsonToFeedMapDatas(mJsonString,0,1)
                detailviewitem_profile_textview.text = feedMapDatas.get(0).Id
                //map_Title.text = MapTitle
                map_Title.text = feedMapDatas.get(0).MapTitle

                class SetImageTask : AsyncTask<Void, Void, String>(){
                    override fun onPreExecute() {
                        super.onPreExecute()
                    }
                    var bm: Bitmap? = null

                    override fun doInBackground(vararg params: Void?): String? {
                        try {
                            val url =
                                URL(feedMapDatas.get(0).MapImage)
                            val conn = url.openConnection()
                            conn.connect()
                            val bis = BufferedInputStream(conn.getInputStream())
                            bm = BitmapFactory.decodeStream(bis)
                            bis.close()
                        } catch (e : java.lang.Exception) {
                            Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                        }
                        return null
                    }

                    override fun onPostExecute(result: String?) {
                        super.onPostExecute(result)
                        //TODO:피드에서 이미지 적용해볼 소스코드
                        map_Image.setImageBitmap(bm)
                    }
                }
                var Start = SetImageTask()
                Start.execute()
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