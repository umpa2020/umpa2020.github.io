package com.umpa2020.tracer.main.start.racing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.network.FBMapImage
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.recycler_nearactivity_item.view.*

class NearRecyclerViewAdapter(private var datas: List<NearMap>, private val progressBar: ProgressBar)
  : RecyclerView.Adapter<NearRecyclerViewAdapter.MyViewHolder>() {
  var context: Context? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_nearactivity_item, parent, false)
    context = parent.context
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int {
    return datas.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    Logg.d("리사이클러뷰가 불러짐")
    val singleItem1 = datas[position]

    val cutted = singleItem1.mapTitle.split("||")
    //데이터 바인딩
    holder.mapTitle.text = cutted[0]
    //holder.distance.text = PrettyDistance().convertPretty(singleItem1.distance)


    FBMapImage().getMapImage(holder.imageView, singleItem1.mapTitle)

    //클릭하면 맵 상세보기 페이지로 이동
    holder.imageView.setOnClickListener(object : OnSingleClickListener{
      override fun onSingleClick(v: View?) {

        val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
        nextIntent.putExtra("MapTitle", singleItem1.mapTitle) //mapTitle 정보 인텐트로 넘김
        context!!.startActivity(nextIntent)
      }
    })
    Logg.d("ssmm11 position == $position / data.size - 1 = ${datas.size-1}")

    if (position == datas.size-1 || position > 4)
      progressBar.dismiss()
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView = view.nearRouteActivityRouteImage
    var mapTitle = view.nearRouteActivityMapTitle
    var distance = view.nearRouteActivityDistanceAway
  }
}

