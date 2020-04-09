package com.umpa2020.tracer.main.profile.myroute


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo

class ProfileUserRouteFragment : Fragment() {
  var titleArray: ArrayList<String> = arrayListOf()
  lateinit var mAdapter: ProfileRecyclerViewAdapterRoute

  //TODO. 현재 사용 안하고 있어서 추 후 구현 필요

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    var view = inflater.inflate(R.layout.recycler_profile_user_route, container, false)
    // 리사이클러뷰 달기
    val mRecyclerView = view.findViewById<RecyclerView>(R.id.profileRrecyclerRoute)
    val mGridLayoutManager = GridLayoutManager(context, 3)
    mRecyclerView.layoutManager = mGridLayoutManager
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()


    val db = FirebaseFirestore.getInstance()

    db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          titleArray.add(document.id)
        }
        //adpater 추가
        //mAdapter = ProfileRecyclerViewAdapterRoute(titleArray)
        //mRecyclerView.adapter = mAdapter

        progressbar.dismiss()
      }
      .addOnFailureListener { exception ->
      }


    return view
  }
}