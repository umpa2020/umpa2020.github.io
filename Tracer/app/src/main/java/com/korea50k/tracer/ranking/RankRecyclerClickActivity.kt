package com.korea50k.tracer.ranking

/*
class RankRecyclerClickActivity : AppCompatActivity() , OnLoadMoreListener {
    var start = 0
    var end = 15
    lateinit var jArray : JSONArray
    override fun onLoadMore() {
        if (rankDetailMapDatas.size > 4 && mAdapter.itemCount < jArray.length()) {
            //mAdapter.setProgressMore(true)
            Handler().postDelayed({
                itemList.clear()

                start = mAdapter.itemCount
                end = start + 15
                mAdapter.addItemMore(itemList)
                mAdapter.setMoreLoading(false)
            }, 100)
        }
    }

    var mJsonString = ""
    var MapTitle = ""
    var MapImage = ""

    lateinit var mAdapter : RankDetailRecyclerViewAdapterMap
    lateinit var itemList : ArrayList<RankDetailMapData>

    lateinit var rankDetailMapDatas : ArrayList<RankDetailMapData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_rank_recycler_click)

        val intent =  getIntent()
        MapTitle = intent.extras?.getString("MapTitle").toString()
        Log.d("Ha..",MapTitle)
        mapName_TextView.text=MapTitle

        val task = GetData()
        task.execute("http://15.164.50.86/playerRankingAboutMapDownload.php?MapTitle="+MapTitle)

        itemList = java.util.ArrayList()
        var mRecyclerView = findViewById<RecyclerView>(R.id.rank_detailRecyclerView)
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
                        Log.d("ranking detail","click listener")


                        val newintent = Intent(baseContext, UserActivity::class.java)
                        newintent.putExtra("ID", ID_TextView.text)
                        startActivity(newintent)
                        finish()

                    }
                })
        )
    }

    fun loadData() {
        itemList.clear()
        mAdapter.addAll(rankDetailMapDatas)
    }

    private fun playerRankingAboutMapDownload(MapTitle: String) {
        RetrofitClient.retrofitService.playerRankingAboutMapDownload(MapTitle).enqueue(object :
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
                Log.d("Json tlqkf",result)
                // just size data , not use
                // please don't remove that
                val jObject = JSONObject(mJsonString)
                jArray = jObject.getJSONArray("JsonData")

                rankDetailMapDatas = ConvertJson.JsonToRankDetailMapDatas(mJsonString, start, end)

                MapImage = rankDetailMapDatas[0].MapImage
                ID_TextView.text = rankDetailMapDatas[0].Id

                class SetImageTask : AsyncTask<Void, Void, String>(){
                    override fun onPreExecute() {
                        super.onPreExecute()
                    }
                    var bm: Bitmap? = null

                    override fun doInBackground(vararg params: Void?): String? {
                        try {
                            val url =
                                URL(MapImage)
                            val conn = url.openConnection()
                            conn.connect()
                            val bis = BufferedInputStream(conn.getInputStream())
                            bm = BitmapFactory.decodeStream(bis)
                            bis.close()

                            playerRankingAboutMapDownload(MapTitle!!)
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
                loadData()
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val serverURL = params[0]
            try {
                val url = URL(serverURL)
                val httpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.setReadTimeout(5000)
                httpURLConnection.setConnectTimeout(5000)
                httpURLConnection.connect()

                val responseStatusCode = httpURLConnection.getResponseCode()

                val inputStream: InputStream
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream()
                } else {
                    inputStream = httpURLConnection.getErrorStream()
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF8")
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

    fun onClick(v: View){
        var newIntent = Intent(this, RunThisMapActivity::class.java)
        newIntent.putExtra("MapTitle",MapTitle)
        startActivity(newIntent)
    }
}

 */