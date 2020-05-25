package com.umpa2020.tracer.main.start.racing

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.main.BaseActivity
import kotlinx.android.synthetic.main.activity_all_ranking.*

class AllRankingActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_all_ranking)

    val arrRankingData = intent.getParcelableArrayListExtra<RankingData>("arrRankingData")!!
    resultPlayerRankingRecycler.layoutManager = LinearLayoutManager(this)
    resultPlayerRankingRecycler.adapter = AllRankingRecyclerViewAdapter(arrRankingData)
  }
}
