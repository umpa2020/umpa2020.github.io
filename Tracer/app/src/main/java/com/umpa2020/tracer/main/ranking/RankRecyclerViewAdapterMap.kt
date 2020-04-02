package com.umpa2020.tracer.main.ranking

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikeMapsData
import com.umpa2020.tracer.network.FBLikes
import com.umpa2020.tracer.util.PrettyDistance
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.recycler_rankfragment_item.view.*

class RankRecyclerViewAdapterMap(val mdata: ArrayList<InfoData>, val mode: String, val progressBar: ProgressBar) : RecyclerView.Adapter<RankRecyclerViewAdapterMap.mViewHolder>() {
  var context: Context? = null
  val GETLIKES = 50
  var likeMapsDatas = arrayListOf<LikeMapsData>()

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val singleItem = mdata[position]
    val ranking = position + 1
    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          GETLIKES -> {
            likeMapsDatas = msg.obj as ArrayList<LikeMapsData>
            //adpater 추가
            for (i in likeMapsDatas) {
              if (i.mapTitle.equals(singleItem.mapTitle)) {
                holder.heart.setImageResource(R.drawable.ic_favorite_red_24dp)
                holder.heartswitch.text = "on"
              }
            }
          }
        }
      }
    }


    val cutted = singleItem.mapTitle!!.split("||")
    //데이터 바인딩
    holder.rank.text = ranking.toString()

    holder.maptitle.text = cutted[0]
    holder.distance.text = PrettyDistance().convertPretty(singleItem.distance!!)
    if (mode.equals("execute")) {
      holder.execute.text = singleItem.execute.toString()
    } else if (mode.equals("likes")) {
      FBLikes().getLikes(mHandler)
      holder.heart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
      holder.execute.text = singleItem.likes.toString()
    }

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
      val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
      nextIntent.putExtra("MapTitle", singleItem.mapTitle) //mapTitle 정보 인텐트로 넘김
      context!!.startActivity(nextIntent)
    }

    holder.heart.setOnClickListener {
      var likes = Integer.parseInt(holder.execute.text.toString())

      if (holder.heartswitch.text.equals("off")) {
        FBLikes().setLikes(singleItem.mapTitle!!, likes)
        holder.heart.setImageResource(R.drawable.ic_favorite_red_24dp)
        likes++
        holder.execute.text = likes.toString()
        holder.heartswitch.text = "on"
      }
      else {
        FBLikes().setminusLikes(singleItem.mapTitle!!, likes)
        holder.heart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        likes--
        holder.execute.text = likes.toString()
        holder.heartswitch.text = "off"
      }
    }

    // 정보를 다 표현하면 dismiss
    // > 5를 한 이유는 recyclerview 특성 상 모든 정보를 한 번에 담는게 아니라
    // 스크롤이 내려가면 달게 posiotion이 증가 되어서 mdata.size 까지
    // 도달하지 못하는 경우가 있음
    // 추후에 코드 정리 할 예정 - 정빈
    if (position == mdata.size-1 || position > 5) {
      progressBar.dismiss()
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_rankfragment_item, parent, false)
    context = parent.context
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반ㅎ환
  override fun getItemCount(): Int {
    //return 10
    return mdata.size
  }

  //여기서 item을 textView에 옮겨줌

  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var rank = view.rankingFragmentCountTextView
    var maptitle = view.rankingFragmentMapTitleTextView
    var distance = view.rankingFragmentDistanceTextView
    var execute = view.rankingFragmentExecuteTextView
    var heart = view.rankingFragmentHeartImageView
    var heartswitch = view.rankingFragmentHeartSwitch
  }


}

