package com.korea50k.tracer.profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.R
import com.korea50k.tracer.util.ProgressBar
import com.korea50k.tracer.util.UserInfo

class ProfileUserRouteFragment : Fragment() {
    lateinit var profileDownloadThread: Thread
    var titleArray : ArrayList<String> = arrayListOf()
    lateinit var mAdapter : ProfileRecyclerViewAdapterRoute


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.recycler_profile_user_route, container, false)
        // 리사이클러뷰 달기
        val mRecyclerView = view.findViewById<RecyclerView>(R.id.profileRrecyclerRoute)
        val mGridLayoutManager = GridLayoutManager(context, 3)
        mRecyclerView.layoutManager = mGridLayoutManager
        val progressbar = ProgressBar(context!!)
        progressbar.show()


        profileDownloadThread = Thread(Runnable {
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


                    Log.d("ssmm11", "title array = " + titleArray)
                    progressbar.dismiss()
                }
                .addOnFailureListener { exception ->
                }
        })

        profileDownloadThread.start()

        return view
    }
}
