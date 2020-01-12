package com.korea50k.RunShare.Activities.Profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.UserMapImageData
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import java.io.BufferedInputStream
import java.net.URL


//  처음 부터 새로하는데
class ProfileMapRecyclerViewAdapter(onLoadMoreListener: FragmentUserRace) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    var itemList=ArrayList<UserMapImageData>()
    private var onLoadMoreListener = onLoadMoreListener
    private var mGridLayoutManager: GridLayoutManager? = null

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
    fun setGridLayoutManager(gridLayoutManager: GridLayoutManager) {
        this.mGridLayoutManager = gridLayoutManager
    }

    fun setRecyclerView(mView: RecyclerView) {
        mView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = recyclerView.childCount
                totalItemCount = mGridLayoutManager!!.itemCount
                firstVisibleItem = mGridLayoutManager!!.findFirstVisibleItemPosition()
                lastVisibleItem = mGridLayoutManager!!.findLastVisibleItemPosition()
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
                R.layout.user_grid_image,
                parent,
                false
            )
        )
    }

    fun addAll(lst: ArrayList<UserMapImageData>) {
        //itemList.clear()
        itemList.addAll(lst)
        notifyDataSetChanged()
    }

    fun addItemMore(lst: List<UserMapImageData>) {
        itemList.addAll(lst)
        notifyItemRangeChanged(0, itemList.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val singleItem = itemList[position]
            var ranking = position+1
            holder.mapTitle.text=singleItem.MapTitle

            //TODO:이미지 해봐야함
            //holder.mapImage.setImageDrawable(singleItem.MapImage)

            Glide.with(holder.mapImage.context).load(singleItem.MapImage).apply(RequestOptions().centerCrop()).into(holder.mapImage)
        }
    }

    fun setMoreLoading(isMoreLoading: Boolean) {
        this.isMoreLoading = isMoreLoading
    }

    internal class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mapImage: ImageView
        var mapTitle: TextView

        init {
            mapTitle = v.findViewById(R.id.gridMapTitle) as TextView
            mapImage = v.findViewById(R.id.gridImage) as ImageView
        }
    }
}

