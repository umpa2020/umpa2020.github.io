package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import com.korea50k.RunShare.Activities.MapDetailActivity
import com.korea50k.RunShare.R
import java.util.function.Consumer

class RankRecyclerClickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)
    }

    fun onClick(v: View){
        var nextIntent= Intent(this,MapDetailActivity::class.java)
        startActivity(nextIntent)
    }
}
