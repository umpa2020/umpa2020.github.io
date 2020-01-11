package com.korea50k.RunShare.Activities.Profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.dataClass.RankMapData
import java.util.ArrayList
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.korea50k.RunShare.dataClass.UserMapImageData

/*
//  처음 부터 새로하는데
class RankMapRecyclerViewAdapter(onLoadMoreListener: OnLoadMoreListener) :
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
        //현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값을 가지고 오기
        val width = parent.resources.displayMetrics.widthPixels / 3
        val imageView = ImageView(parent.context)
        imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    com.korea50k.RunShare.R.layout.user_grid_image,
                    parent,
                    false
                )
            )
    }

    fun addAll(lst: List<UserMapImageData>) {
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
/*
            Glide.with(holder.itemView.context)
                .load(UserMapImageData[position].usermapimage)
                .apply(RequestOptions().centerCrop())
                .into(holder.gridImage)

 */
            Glide.with(holder.itemView.context)
                .load(itemList[position].usermapimage)
                .apply(RequestOptions().centerCrop())
                .into(holder.gridImage)

        }
    }

    fun setMoreLoading(isMoreLoading: Boolean) {
        this.isMoreLoading = isMoreLoading
    }

    internal class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView
        init {
            image = v.findViewById(com.korea50k.RunShare.R.id.gridImage) as ImageView
        }
    }



}

 */
