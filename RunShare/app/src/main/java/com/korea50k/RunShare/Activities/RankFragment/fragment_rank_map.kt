package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.dataClass.RankMapData
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_rank_map.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.fragment_rank_map.*
import kotlinx.android.synthetic.main.fragment_rank_map.view.rank_recycler_map


class fragment_rank_map : Fragment(), RankRecyclerViewAdapter_Map.OnLoadMoreListener {
    lateinit var rankMapDatas : ArrayList<RankMapData>
    lateinit var rankrecyclerviewadapterMap: RankRecyclerViewAdapter_Map

    var mJsonString = ""
    lateinit var mAdapter : RankRecyclerViewAdapter_Map
    lateinit var itemList : ArrayList<RankMapData>
    lateinit var onLoadMoreListener : RankRecyclerViewAdapter_Map.OnLoadMoreListener
    var start = 0
    var end = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    rankDownload("kjb", 0,15)

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(com.korea50k.RunShare.R.layout.fragment_rank_map, container, false)

        itemList = java.util.ArrayList()
        var mRecyclerView = view.findViewById<RecyclerView>(R.id.rank_recycler_map)
        val mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = RankRecyclerViewAdapter_Map(this)
        mAdapter.setLinearLayoutManager(mLayoutManager)
        mAdapter.setRecyclerView(mRecyclerView)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(context!!, mRecyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val intent = Intent(context, RankRecyclerClickActivity::class.java)
                        intent.putExtra("MapTitle", rankMapDatas.get(position).mapTitle)
                        /*intent.putExtra("MapImage", rankMapDatas.get(position).mapImage)
                        intent.putExtra("Id", rankMapDatas.get(position).id)*/
                        startActivity(intent)

                    }
                })
        )
        return view
    }

    private fun rankDownload(Id: String, start : Int, end : Int) {
        RetrofitClient.retrofitService.rankDownload(Id, start, end).enqueue(object :
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

    override fun onLoadMore() {
        Log.d("ssmm11", "onLoadMore")
        //mAdapter.setProgressMore(true)
        Handler().postDelayed({
            itemList.clear()

            start = mAdapter.itemCount
            end = start + 15
            Toast.makeText(context, "more" , Toast.LENGTH_SHORT).show()
            val task = GetData()
            task.execute("http://15.164.50.86/rankDownload.php")

            mAdapter.addItemMore(itemList)
            mAdapter.setMoreLoading(false)
        }, 100)
    }

    fun loadData() {
        itemList.clear()
        mAdapter.addAll(rankMapDatas)
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
                rankMapDatas = ConvertJson.JsonToRankMapDatas(mJsonString, start, end-1)
                Log.d("ssmm11", "rankMapDatas = "+ rankMapDatas)

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
}
