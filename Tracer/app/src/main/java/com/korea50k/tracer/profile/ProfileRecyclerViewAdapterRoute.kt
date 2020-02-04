package com.korea50k.tracer.profile

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R
import com.korea50k.tracer.UserInfo
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.ranking.RankRecyclerItemClickActivity
import kotlinx.android.synthetic.main.recycler_profilefragment_route_grid_image.view.*

class ProfileRecyclerViewAdapterRoute (val mdata :ArrayList<String>) : RecyclerView.Adapter<ProfileRecyclerViewAdapterRoute.mViewHolder>() {
    var context : Context? = null
    //생성된 뷰 홀더에 데이터를 바인딩 해줌.
    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        val singleItem = mdata[position]

        var cutted = singleItem!!.split("||")
        //데이터 바인딩
        // glide imageview 소스


        val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
        val mapImageRef = storage.reference.child("mapImage").child(singleItem!!)

        Log.d("ssmm11" , mapImageRef.toString())
        mapImageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Glide.with(context!!)
                    .load(task.result)
                    .override(1024, 980)
                    .into(holder.image)
            } else {
            }
        }

        holder.maptitle.text = cutted[0]


        //클릭하면 맵 상세보기 페이지로 이동
        holder.itemView.setOnClickListener{
            val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
            nextIntent.putExtra("MapTitle",  singleItem) //mapTitle 정보 인텐트로 넘김
            context!!.startActivity(nextIntent)
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
        Log.d("rank", "데이터 크기 " + mdata.size.toString())
        //return 10 //TODO 갯수 조절 여기서
        return mdata.size
    }

    //여기서 item을 textView에 옮겨줌

    inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view!!) {
        var image = view.profileFragmentRouteGridImage
        var maptitle = view.profileFragmentGridMapTitle
    }
}

