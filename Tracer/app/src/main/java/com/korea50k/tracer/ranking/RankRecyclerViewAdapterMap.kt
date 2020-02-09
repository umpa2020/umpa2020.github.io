package com.korea50k.tracer.ranking

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.recycler_rankfragment_item.view.*

class RankRecyclerViewAdapterMap (val mdata :ArrayList<InfoData>) : RecyclerView.Adapter<RankRecyclerViewAdapterMap.mViewHolder>() {
    var context : Context? = null
    //생성된 뷰 홀더에 데이터를 바인딩 해줌.
    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        val singleItem = mdata[position]
        var ranking = position + 1

        var cutted = singleItem.mapTitle!!.split("||")
        //데이터 바인딩
        holder.rank.text = ranking.toString()
        holder.maptitle.text = cutted[0]
        holder.execute.text = singleItem.execute.toString()

        //ranking에 따라 트로피 색 바뀌게 하는 부분
        if (ranking == 1)
            holder.rank.setBackgroundResource(R.drawable.ic_1)
        else if (ranking == 2)
            holder.rank.setBackgroundResource(R.drawable.ic_2)
        else if (ranking == 3)
            holder.rank.setBackgroundResource(R.drawable.ic_3)
        else
            holder.rank.setBackgroundResource(R.drawable.ic_4)

        //클릭하면 맵 상세보기 페이지로 이동
        holder.itemView.setOnClickListener{
            val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
            nextIntent.putExtra("MapTitle",  singleItem.mapTitle) //mapTitle 정보 인텐트로 넘김
            context!!.startActivity(nextIntent)

            val progressbar = ProgressBar(context!!)
            progressbar.show()
        }
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_rankfragment_item, parent, false)
        Log.d("rank", "onCreateViewHolder호출")
        context = parent.context
        return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
    }

    //item 사이즈, 데이터의 전체 길이 반ㅎ환
    override fun getItemCount(): Int {
        Log.d("rank", "데이터 크기 " + mdata.size.toString())
        //return 10 //TODO 갯수 조절 여기서
        return mdata.size
    }

    //여기서 item을 textView에 옮겨줌

    inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view!!) {
        var rank = view.rankingFragmentCountTextView
        var maptitle = view.rankingFragmentMapTitleTextView
        var execute = view.rankingFragmentExecuteTextView
    }


}

