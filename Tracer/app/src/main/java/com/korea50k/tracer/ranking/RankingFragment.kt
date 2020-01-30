package com.korea50k.tracer.Ranking


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.korea50k.tracer.R
import kotlinx.android.synthetic.main.fragment_ranking.view.*

/**
 * A simple [Fragment] subclass.
 */
class RankingFragment : Fragment() {
    lateinit var mAdapter : RankRecyclerViewAdapterMap
    lateinit var itemList : ArrayList<RecyclerRankingItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view:View = inflater!!.inflate(R.layout.fragment_ranking, container, false)

        /*
        itemList = java.util.ArrayList()
        val mLayoutManager = LinearLayoutManager(context)
        rank_recycler_map.layoutManager = mLayoutManager

        //mAdapter = RankRecyclerViewAdapterMap(this)
        mAdapter.setLinearLayoutManager(mLayoutManager)
        mAdapter.setRecyclerView(rank_recycler_map)

        rank_recycler_map.adapter = mAdapter

         */

        view.nextButton.setOnClickListener{
            val nextIntent = Intent(context!!, RankRecyclerItemClickActivity::class.java)
            startActivity(nextIntent)
        }

        return view
    }

}
