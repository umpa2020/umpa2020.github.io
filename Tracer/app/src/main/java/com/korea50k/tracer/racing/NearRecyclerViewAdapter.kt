package com.korea50k.tracer.racing

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.NearMap
import com.korea50k.tracer.ranking.RankRecyclerItemClickActivity
import kotlinx.android.synthetic.main.recycler_nearactivity_item.view.*

class NearRecyclerViewAdapter(private var datas: ArrayList<NearMap>) : RecyclerView.Adapter<NearRecyclerViewAdapter.MyViewHolder>() {
    var context : Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_nearactivity_item, parent, false)
        context = parent.context
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("rank", "리사이클러뷰가 불러짐")
        val singleItem1 = datas[position]

        //데이터 바인딩
        holder.mapTitle.text = singleItem1.mapTitle
        holder.distance.text = singleItem1.distance.toString()

        //클릭하면 맵 상세보기 페이지로 이동
        holder.itemView.setOnClickListener{
            //TODO 그 루트로 넘어가게 해야함

            val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
            nextIntent.putExtra("MapTitle",  singleItem1.mapTitle) //mapTitle 정보 인텐트로 넘김
            context!!.startActivity(nextIntent)
        }
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view!!) {
        var mapTitle = view.nearRouteActivityMapTitle
        var distance = view.nearRouteActivityDistanceAway
    }
}

