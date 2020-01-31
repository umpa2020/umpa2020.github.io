package com.korea50k.tracer.ranking


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.RecyclerRankingDataItem
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*

/**
 * A simple [Fragment] subclass.
 */
class RankingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View = inflater!!.inflate(R.layout.fragment_ranking, container, false)

        //TODO 데이터 서버에서 받아와야 함
        var mdata = arrayListOf<RecyclerRankingDataItem>(
            RecyclerRankingDataItem("test1", 1),
            RecyclerRankingDataItem("test2", 2),
            RecyclerRankingDataItem("test3", 3),
            RecyclerRankingDataItem("test4", 4),
            RecyclerRankingDataItem("test5", 5),
            RecyclerRankingDataItem("test6", 6),
            RecyclerRankingDataItem("test7", 7),
            RecyclerRankingDataItem("test8", 8),
            RecyclerRankingDataItem("test9", 9),
            RecyclerRankingDataItem("test10", 10),
            RecyclerRankingDataItem("test11", 11),
            RecyclerRankingDataItem("test12", 12),
            RecyclerRankingDataItem("test13", 13),
            RecyclerRankingDataItem("test14", 14),
            RecyclerRankingDataItem("test15", 15),
            RecyclerRankingDataItem("test1", 1),
            RecyclerRankingDataItem("test2", 2),
            RecyclerRankingDataItem("test3", 3),
            RecyclerRankingDataItem("test4", 4),
            RecyclerRankingDataItem("test5", 5),
            RecyclerRankingDataItem("test6", 6),
            RecyclerRankingDataItem("test7", 7),
            RecyclerRankingDataItem("test8", 8),
            RecyclerRankingDataItem("test9", 9),
            RecyclerRankingDataItem("test10", 10),
            RecyclerRankingDataItem("test11", 11),
            RecyclerRankingDataItem("test12", 12),
            RecyclerRankingDataItem("test13", 13),
            RecyclerRankingDataItem("test14", 14),
            RecyclerRankingDataItem("test15", 15)

        )

        //레이아웃 매니저 추가
        view.rank_recycler_map.layoutManager = LinearLayoutManager(context)
        //adpater 추가
        view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(mdata)


        return view
    }

}
