package com.korea50k.RunShare.Activities.RankFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.RankDetailMapData
import com.korea50k.RunShare.dataClass.RankMapData

class RankDetailRecyclerViewAdapter_Map(val context: Context, val rankDetaildata: ArrayList<RankDetailMapData>, val itemClick: (RankMapData) -> Unit) :
    RecyclerView.Adapter<RankDetailRecyclerViewAdapter_Map.Holder>() {

    var i=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_rank_item, parent, false)
        return Holder(view, itemClick)
    }
    override fun getItemCount(): Int {
        return rankDetaildata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(rankDetaildata[position], context)
    }

    inner class Holder(itemView: View?, itemClick: (RankMapData) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
        val mapRank = itemView?.findViewById<TextView>(R.id.rank_cardView_rank)
        val mapMapTitle = itemView?.findViewById<TextView>(R.id.rank_cardView_name)
        val mapTime = itemView?.findViewById<TextView>(R.id.rank_cardView_execute)
        val mapTemp = itemView?.findViewById<TextView>(R.id.rank_cardView_like)

        fun bind (rankmapdata : RankDetailMapData, context: Context) {
            mapRank?.text = (i + 1).toString()
            mapMapTitle?.text = rankmapdata.ChallengerId.toString()
            mapTime?.text = rankmapdata.ChallengerTime.toString()
            mapTemp?.text = rankmapdata.TempData
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

            //itemView.setOnClickListener { itemClick(rankmapdata) }
        }
    }
}