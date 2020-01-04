package com.korea50k.RunShare.MainFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.DataClass.Feed_Users
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.recycler_rank_item.view.*

class Feed_RecyclerAdapter(val userList:ArrayList<Feed_Users>): RecyclerView.Adapter<Feed_RecyclerAdapter.ViewHolder>() {

    //아이템의 갯수를 설정해줍니다 (저 안의 숫자는 보통 .size로 지정해줍니다.)
    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Feed_RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_rank_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: Feed_RecyclerAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data: Feed_Users){
            itemView.rank_cardView_name.text = data.name
            itemView.rank_cardView_execute.text = data.execute.toString()
            itemView.rank_cardView_like.text = data.like.toString()
            //각각의 아이템 클릭할때의 이벤트를 띄워줍니다.
            itemView.setOnClickListener({
                //여기서 토스트메시지를 띄워줍니다.
                Toast.makeText(itemView.context, "'${data.name}'를 클릭했습니다.", Toast.LENGTH_LONG).show()
            })
        }

    }

}