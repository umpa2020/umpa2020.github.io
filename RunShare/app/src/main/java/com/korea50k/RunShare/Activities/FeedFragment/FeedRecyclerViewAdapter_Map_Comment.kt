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

class FeedRecyclerViewAdapter_Map_Comment(val context: Context, val feeddata: ArrayList<FeedMapData>, val itemClick: (FeedMapData) -> Unit) :
    RecyclerView.Adapter<FeedRecyclerViewAdapter_Map_Comment.Holder>() {

    var i=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_feed_map_nocomment, parent, false)
        return Holder(view, itemClick)
    }
    override fun getItemCount(): Int {
        return feeddata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(feeddata[position], context)
    }

    inner class Holder(itemView: View?, itemClick: (FeedMapData) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
        val feedImage = itemView?.findViewById<ImageView>(R.id.detailviewitem_profile_image) // 여기서 마찬가지로 변수 map 이미지만 받아서 넘어갈수 있게끔
        val feedUsername = itemView?.findViewById<TextView>(R.id.detailviewitem_profile_textview)
        val feedMapImage = itemView?.findViewById<ImageView>(R.id.map_Image)
        val feedMapcomment = itemView?.findViewById<TextView>(R.id.map_comment)
        val feedHeart = itemView?.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
        val feedHeartcount = itemView?.findViewById<TextView>(R.id.heart_count)

        fun bind (feedmapdata : FeedMapData, context: Context) {
            //feedImage?.setImageURI() = feedmapdata.MapTitle 나중에 이미지
            feedUsername?.text = feedmapdata.Id
            //feedMapname?.setImageURI() = feedmapdata.MapTitle
            //feedMapImage?.text = feedmapdata.MapTitle ------------------- 추가적으로 이미지만 넣어서 클릭이벤트를 받아야 할거 같아
            feedMapcomment?.text = feedmapdata.MapTitle
            //feedHeart?.setImageURI() = feedmapdata.MapTitle
            feedHeartcount?.text = feedmapdata.Likes

            itemView.setOnClickListener { itemClick(feedmapdata) }
        }
    }
}