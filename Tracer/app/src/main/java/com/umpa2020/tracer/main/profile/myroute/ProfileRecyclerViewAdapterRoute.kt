package com.umpa2020.tracer.main.profile.myroute

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikeMapsData
import com.umpa2020.tracer.extensions.MM_SS
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.network.FBLikes
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.PrettyDistance
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.android.synthetic.main.recycler_profilefragment_route_grid_image.view.*
import java.sql.Timestamp
import java.util.*

class ProfileRecyclerViewAdapterRoute(val mdata: ArrayList<InfoData>) : RecyclerView.Adapter<ProfileRecyclerViewAdapterRoute.mViewHolder>() {
  var context: Context? = null
  val GETPROFILELIKES = 110
  var likeMapsDatas = arrayListOf<LikeMapsData>()


  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {

    val singleItem = mdata[position]

    val cutted = singleItem.mapTitle!!.split("||")
    //데이터 바인딩
    // glide imageview 소스

    //TODO: Network class 테이블에 맞게 클래스 다 만들어 놓기
    // app.getString   google_storage_bucket
    // string에 저장해서 사용 해보았으나
    // Please use a gs:// URL for your Firebase Storage bucket. 에러가 뜨면서 실행이 안되는 문제..
    // val storage = FirebaseStorage.getInstance(R.string.google_storage_bucket_string.toString()) // debug용, release용 구분
    val storage = FirebaseStorage.getInstance() // debug용, release용 구분
    val mapImageRef = storage.reference.child("mapImage").child(singleItem.mapTitle!!)
    mapImageRef.downloadUrl.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Glide 이용하여 이미지뷰에 로딩
        Glide.with(context!!)
          .load(task.result)
          .override(1024, 980)
          .into(holder.image)
      }
    }

    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          GETPROFILELIKES -> {
            likeMapsDatas = msg.obj as ArrayList<LikeMapsData>
            //adpater 추가
            for (i in likeMapsDatas) {
              if (i.mapTitle.equals(singleItem.mapTitle)) {
                holder.heart.setImageResource(R.drawable.ic_favorite_red_24dp)
                holder.heart.tag = R.drawable.ic_favorite_red_24dp
              }
            }
          }
        }
      }
    }
    FBLikes().getLikes(mHandler, "profile")



    holder.maptitle.text = cutted[0]
    holder.distance.text = PrettyDistance().convertPretty(singleItem.distance!!)
    holder.time.text = singleItem.time!!.format(MM_SS)
    holder.likes.text = singleItem.likes.toString()
    holder.excutes.text = singleItem.execute.toString()
    holder.date.text = cutted[1].toLong().format("yyyy-MM-dd HH:mm:ss")
    holder.heart.tag = R.drawable.ic_favorite_border_black_24dp


    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener {
      val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
      nextIntent.putExtra("MapTitle", singleItem.mapTitle) //mapTitle 정보 인텐트로 넘김
      context!!.startActivity(nextIntent)
    }

    holder.heart.setOnClickListener {
      var likes = Integer.parseInt(holder.likes.text.toString())
      Logg.d("ssmm11 눌림 tag = ${holder.heart.tag}")
      when (holder.heart.tag) {
        R.drawable.ic_sneaker_for_running -> {

        }
        R.drawable.ic_favorite_border_black_24dp -> {
          FBLikes().setLikes(singleItem.mapTitle!!, likes)
          holder.heart.setImageResource(R.drawable.ic_favorite_red_24dp)
          holder.heart.tag = R.drawable.ic_favorite_red_24dp
          likes++
          holder.likes.text = likes.toString()
        }
        R.drawable.ic_favorite_red_24dp -> {
          FBLikes().setminusLikes(singleItem.mapTitle!!, likes)
          holder.heart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
          holder.heart.tag = R.drawable.ic_favorite_border_black_24dp
          likes--
          holder.likes.text = likes.toString()
        }
      }
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_profilefragment_route_grid_image, parent, false)
    context = parent.context
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반ㅎ환
  override fun getItemCount(): Int {
    Logg.d("데이터 크기 " + mdata.size.toString())
    //return 10
    return mdata.size
  }

  //여기서 item을 textView에 옮겨줌
  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var date = view.profileFragmentGridMapDate
    var image = view.profileFragmentRouteGridImage
    var maptitle = view.profileFragmentGridMapTitle

    //var nickname = view.profileFragmentNickname
    var distance = view.profileFragmentDistance
    var time = view.profileFragmentTime
    var likes = view.profileFragmentLike
    var excutes = view.profileFragmentExecutes
    var heart = view.profileHeartImage
  }
}
