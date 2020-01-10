package com.korea50k.RunShare.Activities.FeedFragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.FeedMapCommentData
import com.korea50k.RunShare.dataClass.FeedMapData
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import java.io.BufferedInputStream
import java.net.URL


class FeedRecyclerMapComment(val context: Context, val feeddata: ArrayList<FeedMapCommentData>/*, val itemClick: (FeedMapData) -> Unit*/) :
    RecyclerView.Adapter<FeedRecyclerMapComment.Holder>() {

    var i = 0

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.comment_feed_item, parent, false)
        return Holder(view/*, itemClick*/)
    }

    override fun getItemCount(): Int {
        return feeddata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
                Log.d("ssmm11", "click + "+ position)

            }
        }
        holder.bind(feeddata[position], context)

    }

    inner class Holder(itemView: View?/*, itemClick: (FeedMapCommentData) -> Unit*/) :
        RecyclerView.ViewHolder(itemView!!) {


        val UserImage = itemView?.findViewById<ImageView>(R.id.commentviewitem_imageview_profile)
        val UserId = itemView?.findViewById<TextView>(R.id.commentviewitem_textview_profile)
        val MapComment = itemView?.findViewById<TextView>(R.id.commentviewitem_feed_comment)


        fun bind(feedmapcommentdata: FeedMapCommentData, context: Context) {

          //  UserImage?.text = feedmapcommentdata.UserImage 나중에 이미지
            UserId?.text = feedmapcommentdata.UserId
            MapComment?.text = feedmapcommentdata.MapComment

        }
    }
}
