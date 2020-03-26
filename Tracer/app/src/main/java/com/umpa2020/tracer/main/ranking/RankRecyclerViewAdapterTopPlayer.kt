package com.umpa2020.tracer.main.ranking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.recycler_rankfragment_topplayer_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class RankRecyclerViewAdapterTopPlayer(val mydata: ArrayList<RankingData>) : RecyclerView.Adapter<RankRecyclerViewAdapterTopPlayer.myViewHolder>() {
  var context: Context? = null

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: myViewHolder, position: Int) {
    val singleItem1 = mydata[position]
    var ranking = position + 1
    val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)

    //데이터 바인딩
    holder.rank.text = ranking.toString()
    holder.maptitle.text = singleItem1.challengerNickname
    holder.time.text = formatter.format(Date(singleItem1.challengerTime!!))

    //ranking에 따라 트로피 색 바뀌게 하는 부분
    if (ranking == 1) {
      holder.rank.setBackgroundResource(R.drawable.ic_ranking1_black)
      holder.rank.text = ""
    } else if (ranking == 2) {
      holder.rank.setBackgroundResource(R.drawable.ic_ranking2_black)
      holder.rank.text = ""
    } else if (ranking == 3) {
      holder.rank.setBackgroundResource(R.drawable.ic_ranking3_black)
      holder.rank.text = ""
    } else
      holder.rank.setBackgroundResource(R.drawable.ic_4)

    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener {
      //TODO 상대방 프로필 넘어가게 해야함
      /*
      val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
      nextIntent.putExtra("MapTitle",  holder.maptitle.text.toString()) //mapTitle 정보 인텐트로 넘김
      context!!.startActivity(nextIntent)
      */

      Toast.makeText(context!!, "상대방 프로필 넘어가면 됨", Toast.LENGTH_SHORT).show()
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_rankfragment_topplayer_item, parent, false)
    context = parent.context
    Logg.d("onCreateViewHolder호출")
    return myViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반환
  override fun getItemCount(): Int {
    Logg.d( "데이터 크기 " + mydata.size.toString())
    return mydata.size
  }

  //여기서 item을 textView에 옮겨줌

  inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var rank = view.rankRecyclerItemClickCountTextView
    var maptitle = view.rankRecyclerItemClickChallengerNicknameTextView
    var time = view.rankRecyclerItemClickTimeTextView
  }


}

