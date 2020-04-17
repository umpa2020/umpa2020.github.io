package com.umpa2020.tracer.main.profile.myrecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RecordData
import com.umpa2020.tracer.network.FBMapImageRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_profile_user_record_item.view.*

class ProfileRecyclerViewAdapterRecord(private var datas: ArrayList<RecordData>) : RecyclerView.Adapter<ProfileRecyclerViewAdapterRecord.MyViewHolder>() {
  var context: Context? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_profile_user_record_item, parent, false)
    context = parent.context
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int {
    return datas.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val singleItem1 = datas[position]

    //데이터 바인딩
    FBMapImageRepository().getMapImage(holder.mapImageView, "test 로 남겨놔" )
    holder.record.text = singleItem1.record


    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener(object : OnSingleClickListener{
      override fun onSingleClick(v: View?) {
        //TODO 히스토리 누르면 어케?

        /*
        val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
        nextIntent.putExtra("MapTitle",  singleItem1.mapTitle) //mapTitle 정보 인텐트로 넘김
        context!!.startActivity(nextIntent)
         */
      }
    })
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var mapImageView = view.profileUserRecordMapImageView!!
    var record = view.profileUserRecordTextView!!
  }
}

