package com.korea50k.RunShare.Activities.RankFragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.RankDetailMapData
import java.lang.Exception
import java.util.ArrayList

class RankDetailRecyclerViewAdapterMap(onLoadMoreListener: OnLoadMoreListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    var itemList= ArrayList<RankDetailMapData>()
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
                R.layout.recycler_rank_item_about_map,
                parent,
                false
            )
        )
    }

    fun addAll(lst: List<RankDetailMapData>) {
        //itemList.clear()
        itemList.addAll(lst)
        notifyDataSetChanged()
    }

    fun addItemMore(lst: List<RankDetailMapData>) {
        itemList.addAll(lst)
        notifyItemRangeChanged(0, itemList.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val singleItem = itemList[position]
            var ranking = position+1

            holder.rank.setText(ranking.toString())
            holder.name.setText(singleItem.ChallengerId)
            holder.time.setText(singleItem.ChallengerTime)
            if (ranking == 1)
                holder.rank.setBackgroundResource(R.drawable.ic_1)
            else if (ranking == 2)
                holder.rank.setBackgroundResource(R.drawable.ic_2)
            else if (ranking == 3)
                holder.rank.setBackgroundResource(R.drawable.ic_3)
            else
                holder.rank.setBackgroundResource(R.drawable.ic_4)
        }
    }

    fun setMoreLoading(isMoreLoading: Boolean) {
        this.isMoreLoading = isMoreLoading
    }

    internal class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var rank: TextView
        var name: TextView
        var time: TextView

        init {
            rank = v.findViewById(R.id.rank_detail_cardView_rank) as TextView
            name = v.findViewById(R.id.rank_detail_cardView_name) as TextView
            time = v.findViewById(R.id.rank_detail_cardView_time) as TextView
        }
    }

}