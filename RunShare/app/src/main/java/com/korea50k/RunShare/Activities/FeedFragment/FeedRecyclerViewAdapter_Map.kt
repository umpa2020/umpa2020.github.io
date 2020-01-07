package com.korea50k.RunShare.Activities.FeedFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.FeedMapData


class FeedRecyclerViewAdapter_Map(val context: Context, val rankdata: ArrayList<FeedMapData>/*, val itemClick: (FeedMapData) -> Unit*/) :
    RecyclerView.Adapter<FeedRecyclerViewAdapter_Map.Holder>() {

    var i=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_feed_map_nocomment, parent, false)
        return Holder(view/*, itemClick*/)
    }
    override fun getItemCount(): Int {
        return rankdata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(rankdata[position], context)
    }

    inner class Holder(itemView: View?/*, itemClick: (FeedMapData) -> Unit*/) : RecyclerView.ViewHolder(itemView!!) {
        val feedImage = itemView?.findViewById<ImageView>(R.id.detailviewitem_profile_image)
        val feedUsername = itemView?.findViewById<TextView>(R.id.detailviewitem_profile_textview)
        val feedMapname = itemView?.findViewById<ImageView>(R.id.map_name)
        val feedMapcomment = itemView?.findViewById<TextView>(R.id.map_comment)
        val feedHeart = itemView?.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
        val feedHeartcount = itemView?.findViewById<TextView>(R.id.heart_count)


        fun bind (feedmapdata : FeedMapData, context: Context) {
            //feedImage?.setImageURI() = feedmapdata.MapTitle 나중에 이미지
            feedUsername?.text = feedmapdata.Uname
            //feedMapname?.setImageURI() = feedmapdata.MapTitle
            feedMapcomment?.text = feedmapdata.Mcomment
            //feedHeart?.setImageURI() = feedmapdata.MapTitle
            feedHeartcount?.text = feedmapdata.HCount

//            itemView.setOnClickListener { itemClick(feedmapdata) }
        }
    }
}