package com.korea50k.RunShare.Activities.Racing

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import com.korea50k.RunShare.Activities.RankFragment.RankDetailRecyclerViewAdapterMap
import com.korea50k.RunShare.Activities.RankFragment.RecyclerItemClickListener
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.Util.map.ViewerMap
import com.korea50k.RunShare.dataClass.RankDetailMapData
import kotlinx.android.synthetic.main.activity_racing_finish.*
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.activity_run_this_map.*
import kotlinx.android.synthetic.main.recycler_rank_item.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class RacingFinishActivity : AppCompatActivity() ,
    RankDetailRecyclerViewAdapterMap.OnLoadMoreListener {

    var start = 0
    var end = 15

    lateinit var racerData: RunningData
    lateinit var makerData: RunningData

    var mJsonString = ""
    var MapTitle = ""

    lateinit var mAdapter : RankDetailRecyclerViewAdapterMap
    lateinit var itemList : ArrayList<RankDetailMapData>

    lateinit var rankDetailMapDatas : ArrayList<RankDetailMapData>

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

        //TODO:정빈 시작
        MapTitle = makerData.mapTitle


        val task = GetData()
        task.execute("http://15.164.50.86/myRankingByMap.php?"+"Id="+SharedPreValue.getNicknameData(baseContext)+"&&MapTitle="+makerData.mapTitle)

        itemList = java.util.ArrayList()
        var mRecyclerView = findViewById<RecyclerView>(R.id.resultPlayerRankingRecycler)
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = RankDetailRecyclerViewAdapterMap(this)
        mAdapter.setLinearLayoutManager(mLayoutManager)
        mAdapter.setRecyclerView(mRecyclerView)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this!!, mRecyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Log.d("ranking","click listener")

                        //TODO:프로필 액티비티 해야한댔나
                        //val intent = Intent(this, RankRecyclerClickActivity::class.java)
                        intent.putExtra("MapTitle", view.rank_cardView_name.text)
                        startActivity(intent)
                    }
                })
        )

        //TODO: resultPlayerRankingRecycler

        /*//TODO:서버에서 해당 맵 랭크 받아오기

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

                                *//*var rankDetailMapDatas =
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
*//*
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
    private fun myRankingByMap(Id : String, MapTitle: String) {
        RetrofitClient.retrofitService.myRankingByMap(Id, MapTitle).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    Log.d("server","Players Ranking About Map")
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

    override fun onLoadMore() {
        Log.d("ssmm11", "onLoadMore , rankDetailMapDatas.size = " + rankDetailMapDatas.size)

        if (rankDetailMapDatas.size > 4) {
            //mAdapter.setProgressMore(true)
            Handler().postDelayed({
                itemList.clear()

                start = mAdapter.itemCount
                end = start + 15
                Toast.makeText(this, "more", Toast.LENGTH_SHORT).show()

                mAdapter.addItemMore(itemList)
                mAdapter.setMoreLoading(false)
            }, 100)
        }
    }

    fun loadData() {
        itemList.clear()
        mAdapter.addAll(rankDetailMapDatas)
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
                rankDetailMapDatas = ConvertJson.JsonToRankDetailMapDatas(mJsonString, start, end)

                Log.d("ssmm11", "받아온 거  = "  + mJsonString)
                //ID_TextView.text = rankDetailMapDatas[0].Id
                //MapImage = rankDetailMapDatas[0].MapImage

                class SetImageTask : AsyncTask<Void, Void, String>(){
                    override fun onPreExecute() {
                        super.onPreExecute()
                    }
                    var bm: Bitmap? = null

                    override fun doInBackground(vararg params: Void?): String? {
                        try {
                            /*val url =
                                URL(MapImage)
                            Log.d("ssmm11", "urlll = "+url)
                            val conn = url.openConnection()
                            conn.connect()
                            val bis = BufferedInputStream(conn.getInputStream())
                            bm = BitmapFactory.decodeStream(bis)
                            bis.close()*/

                            myRankingByMap(SharedPreValue.getNicknameData(baseContext)!!,MapTitle)
                        } catch (e : java.lang.Exception) {
                            Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                        }
                        return null
                    }

                    override fun onPostExecute(result: String?) {
                        super.onPostExecute(result)
                        //TODO:피드에서 이미지 적용해볼 소스코드
                        //rank_root_preview.setImageBitmap(bm)
                    }
                }
                var Start = SetImageTask()
                Start.execute()
                loadData()
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
