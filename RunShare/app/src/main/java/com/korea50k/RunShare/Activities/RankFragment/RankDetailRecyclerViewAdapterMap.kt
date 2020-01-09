package com.korea50k.RunShare.Activities.RankFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.RankDetailMapData

class RankDetailRecyclerViewAdapterMap(val context: Context, val rankDetaildata: ArrayList<RankDetailMapData>, val itemClick: (RankDetailMapData) -> Unit) :
    RecyclerView.Adapter<RankDetailRecyclerViewAdapterMap.Holder>() {

    var i=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_rank_item_about_map, parent, false)
        return Holder(view, itemClick)
    }
    override fun getItemCount(): Int {
        return rankDetaildata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(rankDetaildata[position], context)
    }

    inner class Holder(itemView: View?, itemClick: (RankDetailMapData) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
        val mapRank = itemView?.findViewById<TextView>(R.id.rank_cardView_rank)
        var id=itemView?.findViewById<TextView>(R.id.rank_cardView_name)
        val mapTime = itemView?.findViewById<TextView>(R.id.rank_cardView_time)

        fun bind (rankDetailMapData: RankDetailMapData, context: Context) {
            mapRank?.text = (i + 1).toString()
            id?.text=rankDetailMapData.ChallengerId
            mapTime?.text = rankDetailMapData.ChallengerTime
            i++
            if(i==1){
                mapRank?.setBackgroundResource(R.drawable.ic_1)
            }
            else if(i==2){
                mapRank?.setBackgroundResource(R.drawable.ic_2)
            }
            else if(i==3){
                mapRank?.setBackgroundResource(R.drawable.ic_3)
            }
            else{
                mapRank?.setBackgroundResource(R.drawable.ic_4)
            }

            //TODO 리사이클러뷰 클릭시 상대방 프로필로 넘어가게
            itemView.setOnClickListener { itemClick(rankDetailMapData) }
        }
    }
}