package com.korea50k.RunShare.Activities.RankFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.RankMapData

class RankRecyclerViewAdapter_Map(val context: Context, val rankdata: ArrayList<RankMapData>, val itemClick: (RankMapData) -> Unit) :
    RecyclerView.Adapter<RankRecyclerViewAdapter_Map.Holder>() {

    var i=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_rank_item, parent, false)
        return Holder(view, itemClick)
    }
    override fun getItemCount(): Int {
        return rankdata.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(rankdata[position], context)
    }

    inner class Holder(itemView: View?, itemClick: (RankMapData) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
        val mapRank = itemView?.findViewById<TextView>(R.id.rank_cardView_rank)
        val mapName = itemView?.findViewById<TextView>(R.id.rank_cardView_name)
        val mapExecute = itemView?.findViewById<TextView>(R.id.rank_cardView_execute)
        val mapLike = itemView?.findViewById<TextView>(R.id.rank_cardView_like)

        fun bind (rankmapdata : RankMapData, context: Context) {
            mapRank?.text = (i + 1).toString()
            mapName?.text = rankmapdata.mapTitle
            mapExecute?.text = rankmapdata.excute.toString()
            mapLike?.text = rankmapdata.likes.toString()
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

            itemView.setOnClickListener { itemClick(rankmapdata) }
        }
    }
}