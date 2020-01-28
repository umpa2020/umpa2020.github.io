package com.korea50k.tracer.Ranking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.korea50k.tracer.R
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*

class RankRecyclerItemClickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_item_click)

        rankRecyclerMoreButton.setOnClickListener{
            val nextIntent = Intent(this, RankingMapDetailActivity::class.java)
            startActivity(nextIntent)
        }
    }
}
