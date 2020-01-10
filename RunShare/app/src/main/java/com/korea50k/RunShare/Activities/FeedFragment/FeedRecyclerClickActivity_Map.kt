package com.korea50k.RunShare.Activities.FeedFragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.FeedMapCommentData
import kotlinx.android.synthetic.main.activity_feed_map_comment.*
import kotlinx.android.synthetic.main.comment_feed_item.*


class FeedRecyclerClickActivity_Map : AppCompatActivity() {

    var Comment = ArrayList<FeedMapCommentData>()
    var mAdapter_Map = FeedRecyclerMapComment(this, Comment)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_map_comment)

        val intent =  getIntent()
        val Id = intent.extras?.getString("Id")
        val MapTitle = intent.extras?.getString("MapTitle")
        val Heart = intent.extras?.getString("Heart")
        val Likes = intent.extras?.getString("Likes")
        val MapImage = intent.extras?.getString("MapImage")

        feed_map_recycler_comment!!.adapter = mAdapter_Map
        val lm = LinearLayoutManager(this)
        feed_map_recycler_comment!!.layoutManager = lm
        feed_map_recycler_comment!!.setHasFixedSize(true)

        feed_map_comment_button.setOnClickListener(View.OnClickListener {

            feed_map_comment_editmessage.text.toString()
            Comment.add(FeedMapCommentData())

            mAdapter_Map.notifyDataSetChanged()

            feed_map_comment_editmessage.setText("")
            }
        )
    }
}