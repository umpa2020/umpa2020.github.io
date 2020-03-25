package com.umpa2020.tracer.main.start.racing

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.util.PrettyDistance
import kotlinx.android.synthetic.main.recycler_nearactivity_item.view.*

class NearRecyclerViewAdapter(private var datas: List<NearMap>) : RecyclerView.Adapter<NearRecyclerViewAdapter.MyViewHolder>() {
  var context: Context? = null
  lateinit var mapImageDownloadThread: Thread

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

    var cutted = singleItem1.mapTitle.split("||")
    //데이터 바인딩
    holder.mapTitle.text = cutted[0]
    holder.distance.text = PrettyDistance().convertPretty(singleItem1.distance)


    mapImageDownloadThread = Thread(Runnable {
      // glide imageview 소스
      val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
      val mapImageRef = storage.reference.child("mapImage").child(singleItem1.mapTitle)

      mapImageRef.downloadUrl.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          // Glide 이용하여 이미지뷰에 로딩
          Glide.with(context!!)
            .load(task.result)
            .override(1024, 980)
            .into(holder.imageView)
        } else {
        }
      }
    })
    mapImageDownloadThread.start()

    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener {
      //TODO 그 루트로 넘어가게 해야함

      val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
      nextIntent.putExtra("MapTitle", singleItem1.mapTitle) //mapTitle 정보 인텐트로 넘김
      context!!.startActivity(nextIntent)
    }
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view!!) {
    var imageView = view.nearRouteActivityRouteImage
    var mapTitle = view.nearRouteActivityMapTitle
    var distance = view.nearRouteActivityDistanceAway
  }
}

