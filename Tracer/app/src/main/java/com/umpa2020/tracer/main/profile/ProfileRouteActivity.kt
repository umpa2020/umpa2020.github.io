package com.umpa2020.tracer.main.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile_route.*

class ProfileRouteActivity : AppCompatActivity() {
  lateinit var profileDownloadThread: Thread
  var infoData: ArrayList<InfoData> = arrayListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_route)

    val progressbar = ProgressBar(this)
    progressbar.show()

    profileDownloadThread = Thread(Runnable {
      val db = FirebaseFirestore.getInstance()

//            db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname)
      db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname).whereEqualTo("privacy", "RACING")
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            val data = document.toObject(InfoData::class.java)
            infoData.add(data)
          }
          if (result.isEmpty == true) {
            profileRecyclerRouteisEmpty.visibility = View.VISIBLE
          } else {
            profileRecyclerRouteisEmpty.visibility = View.GONE
          }
          //adpater 추가
          profileRecyclerRoute.adapter = ProfileRecyclerViewAdapterRoute(infoData)
          profileRecyclerRoute.layoutManager = LinearLayoutManager(this)
          progressbar.dismiss()
        }
        .addOnFailureListener { exception ->
        }
    })
    profileDownloadThread.start()
  }
}