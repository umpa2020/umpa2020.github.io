package com.korea50k.tracer.ranking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.RankRecyclerItemClickItem
import com.korea50k.tracer.dataClass.RecyclerRankingDataItem
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*

class RankRecyclerItemClickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_item_click)

        val intent = getIntent()
        //전달 받은 값으로 Title 설정
        var mapTitle = intent.extras?.getString("MapTitle").toString()
        rankRecyclerMapTitle.text = mapTitle

        //TODO 데이터 서버에서 받아와야 함
        var mdata = arrayListOf<RankRecyclerItemClickItem>(
            RankRecyclerItemClickItem("test1", 111),
            RankRecyclerItemClickItem("test2", 2222),
            RankRecyclerItemClickItem("test3", 3333),
            RankRecyclerItemClickItem("test4", 4444),
            RankRecyclerItemClickItem("test5", 5555),
            RankRecyclerItemClickItem("test6", 6666),
            RankRecyclerItemClickItem("test7", 7777),
            RankRecyclerItemClickItem("test8", 8),
            RankRecyclerItemClickItem("test9", 9),
            RankRecyclerItemClickItem("test10", 10),
            RankRecyclerItemClickItem("test11", 11),
            RankRecyclerItemClickItem("test12", 12)
        )

        //레이아웃 매니저 추가
        rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(this)
        //adpater 추가
        rankRecyclerItemClickRecyclerView.adapter = RankRecyclerViewAdapterTopPlayer(mdata)


        rankRecyclerMoreButton.setOnClickListener{
            val nextIntent = Intent(this, RankingMapDetailActivity::class.java)
            nextIntent.putExtra("MapTitle", rankRecyclerMapTitle.text.toString())
            startActivity(nextIntent)
        }
    }
}
