package com.korea50k.RunShare.Activities.Profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.ContentDTO
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.fragment_user_race.view.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FragmentUserRace : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var fragmentView = inflater.inflate(R.layout.fragment_user_race, container, false)

        return fragmentView
        Log.d("grid", "onCreateView 호출 됨")
    }
/*
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs: ArrayList<ContentDTO>

        init {

            contentDTOs = ArrayList()
            Log.d("grid", "init 호출 됨")

            /*
            // 나의 사진만 찾기
            recyclerListenerRegistration = firestore?.collection("images")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }

                account_tv_post_count.text = contentDTOs.size.toString()
                notifyDataSetChanged()

            }

             */

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            return CustomViewHolder(imageView)

            Log.d("grid", "onCreateViewHolder 호출 됨")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageView

            /*
            //TODO 서버에서 이미지 받아서 Glide로 이미지 붙이면 됨
            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageview)

             */
            imageview.setImageResource(R.drawable.maptest)

            Log.d("grid", "onBindViewHolder 호출 됨")
        }

        override fun getItemCount(): Int {

            return contentDTOs.size
            Log.d("grid", "getItemCount 호출 됨")
        }

        // RecyclerView Adapter - View Holder
        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }



 */
}
