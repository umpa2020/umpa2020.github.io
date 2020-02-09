package com.korea50k.tracer.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile_route.*

class ProfileRouteActivity : AppCompatActivity() {
    lateinit var profileDownloadThread: Thread
    var infoData: ArrayList<InfoData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_route)

        profileDownloadThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()

            db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val data = document.toObject(InfoData::class.java)
                        infoData.add(data)
                    }
                    //adpater 추가
                    profileRrecyclerRoute.adapter = ProfileRecyclerViewAdapterRoute(infoData)
                    profileRrecyclerRoute.layoutManager = LinearLayoutManager(this)
                }
                .addOnFailureListener { exception ->
                }
        })

        profileDownloadThread.start()
    }
}
