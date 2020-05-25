package com.umpa2020.tracer.main.profile.myroute

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBLikesRepository
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.recycler_profilefragment_route_grid_image.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class ProfileRouteRecyclerViewAdapter(val mdata: ArrayList<MapInfo>) :
  RecyclerView.Adapter<ProfileRouteRecyclerViewAdapter.mViewHolder>(), CoroutineScope by MainScope() {
  var context: Context? = null

  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val infoData = mdata[position]

    launch {
      holder.image.image(FBMapRepository().getMapImage(infoData.mapId))
    }
    holder.maptitle.text = infoData.mapTitle
    holder.distance.text = infoData.distance.prettyDistance
    holder.time.text = infoData.time.format(mm_ss)
    holder.likes.text = infoData.likes.toString()
    holder.excutes.text = infoData.plays.toString()
    holder.date.text = infoData.createTime.format(Y_M_D)

    if (infoData.played) {
      holder.play.setColorFilter(R.color.colorPrimary)
    }

    if (infoData.liked) {
      holder.heart.setImageResource(R.drawable.ic_favorite_red_24dp)
    } else {
      holder.heart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
    }

    holder.heart.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        launch { FBLikesRepository().toggleLikes(UserInfo.autoLoginKey, infoData.mapId) }
        if (infoData.liked) {
          infoData.liked = false
          infoData.likes--
        } else {
          infoData.liked = true
          infoData.likes++
        }
        notifyItemChanged(position)
      }
    })
    holder.itemView.setOnClickListener(
      object : OnSingleClickListener {
        override fun onSingleClick(v: View?) {
          val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
          nextIntent.putExtra(MAP_ID, infoData.mapId) //mapTitle 정보 인텐트로 넘김
          context!!.startActivity(nextIntent)
        }
      })
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_profilefragment_route_grid_image, parent, false)
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
    var date = view.profileFragmentGridMapDate
    var image = view.profileFragmentRouteGridImage
    var maptitle = view.profileFragmentGridMapTitle
    var distance = view.profileFragmentDistance
    var time = view.profileFragmentTime
    var likes = view.profileFragmentLike
    var excutes = view.profileFragmentExecutes
    var heart = view.profileHeartImage
    var play = view.profileFragmentPlay
  }
}

