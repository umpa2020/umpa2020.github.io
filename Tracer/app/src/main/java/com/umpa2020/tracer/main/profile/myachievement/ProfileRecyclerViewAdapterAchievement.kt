package com.umpa2020.tracer.main.profile.myachievement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.EmblemData
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.network.FBStorageRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_profile_achivement_item.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileRecyclerViewAdapterAchievement(private val emblemDatas: MutableList<EmblemData>) :
  RecyclerView.Adapter<ProfileRecyclerViewAdapterAchievement.MyViewHolder>() {
  var context: Context? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_profile_achivement_item, parent, false)
    context = parent.context
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int {
    return emblemDatas.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val emblemData = emblemDatas[position]

    MainScope().launch {
      holder.mapImageView.image(FBStorageRepository().downloadFile(emblemData.imagePath!!))
    }
    holder.activityText.text = emblemData.name

    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        /*val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
        nextIntent.putExtra("MapId", singleItem1.mapId) //mapTitle 정보 인텐트로 넘김
        context!!.startActivity(nextIntent)*/
      }
    })
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var mapImageView = view.profileUserAchievementImageView!!
    var activityText = view.profileUserAchievementTextView!!
  }
}

