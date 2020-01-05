package com.korea50k.RunShare.Activities.RankFragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.fragment_rank.view.*

class RankRecyclerClickActivity : AppCompatActivity() {
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)

        rank_topplayer_optionButton.setOnClickListener{
            count++//누를때마다 증가
            Toast.makeText(this, "눌림", Toast.LENGTH_SHORT).show()
            Log.d("asdf", count.toString())


            if(count % 2 == 1){
                rank_TopplayerChoice_linearlayout.visibility = View.VISIBLE
                rank_topplayer_optionButton.setBackgroundResource(R.drawable.ic_up_button)
            }
            if(count % 2 == 0){
                rank_TopplayerChoice_linearlayout.visibility = View.GONE
                rank_topplayer_optionButton.setBackgroundResource(R.drawable.ic_down_button)
            }
        }
    }



}
