package com.umpa2020.tracer.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.HistoryData
import kotlinx.android.synthetic.main.recycler_profile_user_history_item.view.*

class ProfileRecyclerViewAdapterHistory(private var datas: ArrayList<HistoryData>) : RecyclerView.Adapter<ProfileRecyclerViewAdapterHistory.MyViewHolder>() {
    var context : Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_profile_user_history_item, parent, false)
        context = parent.context
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val singleItem1 = datas[position]

        //데이터 바인딩
        holder.mapTitle.text = singleItem1.mapTitle
        holder.testvalues.text = singleItem1.testvalues

        //클릭하면 맵 상세보기 페이지로 이동
        holder.itemView.setOnClickListener{
            //TODO 히스토리 누르면 어케?
            Toast.makeText(context!!, "누름", Toast.LENGTH_SHORT).show()

            /*
            val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
            nextIntent.putExtra("MapTitle",  singleItem1.mapTitle) //mapTitle 정보 인텐트로 넘김
            context!!.startActivity(nextIntent)

             */
        }
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view!!) {
        var mapTitle = view.profileFragmentHistoryTextView1
        var testvalues = view.profileFragmentHistoryTextView2
    }
}

