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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.FeedMapData
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import java.io.BufferedInputStream
import java.net.URL


class FeedRecyclerViewAdapter_Map(val context: Context, val feeddata: ArrayList<FeedMapData>/*, val itemClick: (FeedMapData) -> Unit*/) :
    RecyclerView.Adapter<FeedRecyclerViewAdapter_Map.Holder>() {

    var i=0
    interface ItemClick
    {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_feed_map_nocomment, parent, false)
        return Holder(view/*, itemClick*/)
    }
    override fun getItemCount(): Int {
        return feeddata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if(itemClick != null)
        {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        holder.bind(feeddata[position], context)

    }

    inner class Holder(itemView: View?/*, itemClick: (FeedMapData) -> Unit*/) : RecyclerView.ViewHolder(itemView!!) {
        val feedImage = itemView?.findViewById<ImageView>(R.id.detailviewitem_profile_image)
        val feedUsername = itemView?.findViewById<TextView>(R.id.detailviewitem_profile_textview)
        val feedMapImage = itemView?.findViewById<ImageView>(R.id.map_Image)
        val feedMapcomment = itemView?.findViewById<TextView>(R.id.map_comment)
        val feedHeart = itemView?.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
        val feedHeartcount = itemView?.findViewById<TextView>(R.id.heart_count)


        fun bind (feedmapdata : FeedMapData, context: Context) {
            //feedImage?.setImageURI() = feedmapdata.MapTitle 나중에 이미지
            feedUsername?.text = feedmapdata.Id

            feedHeart?.setOnClickListener(View.OnClickListener {
                var before = feedHeartcount?.text.toString().toInt()

                if (feedHeart.tag.toString().equals("0")) {
                    before++
                    feedHeartcount?.text = before.toString()
                    feedHeart.setImageResource(R.drawable.ic_favorite)
                    feedHeart.setColorFilter(
                        ContextCompat.getColor(context, R.color.colorAccent),
                        android.graphics.PorterDuff.Mode.SRC_IN)
                    feedHeart.tag = "1"
                }

                else {
                    before--
                    feedHeartcount?.text = before.toString()
                    feedHeart.setImageResource(R.drawable.ic_favorite_border)
                    feedHeart.setColorFilter(
                        ContextCompat.getColor(context, R.color.black),
                        android.graphics.PorterDuff.Mode.SRC_IN)
                    feedHeart.tag = "0"
                }


            })
            //feedMapname?.setImageURI() = feedmapdata.MapTitle
            feedMapcomment?.text = feedmapdata.MapTitle
            //feedHeart?.setImageURI() = feedmapdata.MapTitle
            feedHeartcount?.text = feedmapdata.Likes
            class SetImageTask : AsyncTask<Void, Void, String>(){
                override fun onPreExecute() {
                    super.onPreExecute()
                }
                lateinit var bm: Bitmap

                override fun doInBackground(vararg params: Void?): String? {
                    try {
                        val url =
                            URL(feedmapdata.MapImage)
                        val conn = url.openConnection()
                        conn.connect()
                        val bis = BufferedInputStream(conn.getInputStream())
                        bm = BitmapFactory.decodeStream(bis)
                        bis.close()
                    } catch (e : java.lang.Exception) {
                        Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                    }
                    return null
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                    //TODO:피드에서 이미지 적용해볼 소스코드
                    feedMapImage!!.setImageBitmap(bm)
                }
            }
            var Start = SetImageTask()
            Start.execute()
//            itemView.setOnClickListener { itemClick(feedmapdata) }
        }
    }
}