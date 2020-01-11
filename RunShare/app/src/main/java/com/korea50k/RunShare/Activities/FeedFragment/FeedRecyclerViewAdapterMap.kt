package com.korea50k.RunShare.Activities.FeedFragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.FeedMapData
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.URL


class FeedRecyclerViewAdapterMap(onLoadMoreListener: fragment_feed_map) :
RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    var itemList= ArrayList<FeedMapData>()
    private var onLoadMoreListener = onLoadMoreListener
    private var mLinearLayoutManager: LinearLayoutManager? = null

    private var isMoreLoading = false
    private val visibleThreshold = 1
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    internal var lastVisibleItem: Int = 0

    //val itemCount = itemList.size

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    init {}

    fun setLinearLayoutManager(linearLayoutManager: LinearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager
    }

    fun setRecyclerView(mView: RecyclerView) {
        mView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = recyclerView.childCount
                totalItemCount = mLinearLayoutManager!!.itemCount
                firstVisibleItem = mLinearLayoutManager!!.findFirstVisibleItemPosition()
                lastVisibleItem = mLinearLayoutManager!!.findLastVisibleItemPosition()
                Log.d("total", totalItemCount.toString() + "")
                Log.d("visible", visibleItemCount.toString() + "")

                Log.d("first", firstVisibleItem.toString() + "")
                Log.d("last", lastVisibleItem.toString() + "")

                if (!isMoreLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    onLoadMoreListener?.onLoadMore()
                    isMoreLoading = true
                }

            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_feed_map_nocomment,
                parent,
                false
            )
        )
    }

    fun addAll(lst: List<FeedMapData>) {
        //itemList.clear()
        itemList.addAll(lst)
        notifyDataSetChanged()
    }

    fun addItemMore(lst: List<FeedMapData>) {
        itemList.addAll(lst)
        notifyItemRangeChanged(0, itemList.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val singleItem = itemList[position]
            holder.feedUsername.setText(singleItem.Id)
            holder.feedMapName.setText(singleItem.MapTitle)
            //holder.feedHeart.setText(singleItem.Heart)
            holder.feedHeartcount.setText(singleItem.Likes)

            //TODO:적용이 안되네 ..
            holder.feedHeart?.setOnClickListener(View.OnClickListener {
                Log.d("ssmm11", "click")
                var before = holder.feedHeartcount?.text.toString().toInt()

                if (holder.feedHeart.tag.toString().equals("0")) {
                    before++
                    holder.feedHeartcount?.text = before.toString()
                    holder.feedHeart.setImageResource(R.drawable.ic_favorite)
                    holder.feedHeart.setColorFilter(
                        Color.parseColor("#ffff0000"), PorterDuff.Mode.SRC_IN)
                    holder.feedHeart.tag = "1"
                } else {
                    before--
                    holder.feedHeartcount?.text = before.toString()
                    holder.feedHeart.setImageResource(R.drawable.ic_favorite_border)
                    holder.feedHeart.setColorFilter(null)
                    holder.feedHeart.tag = "0"
                }


            })

            class SetImageTask : AsyncTask<Void, Void, String>() {
                override fun onPreExecute() {
                    super.onPreExecute()
                }
                var bm:Bitmap?=null
                override fun doInBackground(vararg params: Void?): String? {
                    try {
                        val url =
                            URL(singleItem.MapImage)
                        val conn = url.openConnection()
                        conn.connect()
                        val bis = BufferedInputStream(conn.getInputStream())
                        bm = BitmapFactory.decodeStream(bis)
                        bis.close()
                    } catch (e: java.lang.Exception) {
                        Log.d("ssmm11", "이미지 다운로드 실패 " + e.toString())
                    }
                    return null
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                    //TODO:피드에서 이미지 적용해볼 소스코드
                    if (bm != null)
                        holder.feedMapImage!!.setImageBitmap(bm)
                }
            }

            var Start = SetImageTask()
            Start.execute()
        }
    }

    fun setMoreLoading(isMoreLoading: Boolean) {
        this.isMoreLoading = isMoreLoading
    }

    internal class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var feedUsername : TextView
        var feedMapImage : ImageView
        var feedMapName : TextView
        var feedHeart :ImageView
        var feedHeartcount : TextView

        init{
            feedUsername = v.findViewById(R.id.user_name) as TextView
            feedMapImage = v.findViewById(R.id.map_Image) as ImageView
            feedMapName = v.findViewById(R.id.map_name) as TextView
            feedHeart = v.findViewById(R.id.detailviewitem_favorite_imageview) as ImageView
            feedHeartcount = v.findViewById(R.id.heart_count) as TextView
        }
    }
}