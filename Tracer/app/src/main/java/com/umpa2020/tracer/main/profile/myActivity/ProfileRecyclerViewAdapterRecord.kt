package com.umpa2020.tracer.main.profile.myActivity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_profile_user_record_item.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileRecyclerViewAdapterRecord(val datas: MutableList<ActivityData>) :
  RecyclerView.Adapter<ProfileRecyclerViewAdapterRecord.MyViewHolder>() {
  var context: Context? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_profile_user_record_item, parent, false)
    context = parent.context
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int {
    return datas.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val singleItem1 = datas[position]

    MainScope().launch {
      holder.mapImageView.image(FBMapRepository().getMapImage(singleItem1.mapId!!))
      FBMapRepository().getMapTitle(singleItem1.mapId)?.let {
        val time = singleItem1.time!!.toLong().format(Y_M_D)
        when (singleItem1.mode) {
          BaseFB.ActivityMode.RACING_SUCCESS -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.racing_go_the_distance), it, time)
          }
          BaseFB.ActivityMode.RACING_FAIL -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.racing_fail), it, time)
          }
          BaseFB.ActivityMode.MAP_SAVE -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.map_save), it, time)
          }
        }
      }
    }

    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
        nextIntent.putExtra("mapId", singleItem1.mapId) //mapTitle 정보 인텐트로 넘김
        context!!.startActivity(nextIntent)
      }
    })
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var mapImageView = view.profileUserActivityMapImageView!!
    var activityText = view.profileUserActivityTextView!!
  }
}

