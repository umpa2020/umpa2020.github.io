package com.umpa2020.tracer.network

import android.app.Activity
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.main.profile.ProfileRecyclerViewAdapterRoute
import com.umpa2020.tracer.util.PrettyDistance
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile_route.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Profile fragment 에서 프로필을 설정하는 네트워크 클래스
 *
 */

class GetProfile {


  fun setProfile(view: View) {
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()
    var profileImagePath = "init"

    val db = FirebaseFirestore.getInstance()

    // 총 거리, 총 시간을 구하기 위해서 db에 접근하여 일단 먼저
    // 이용자가 뛴 다른 사람의 맵을 구함
    db.collection("userinfo").document(UserInfo.email).collection("user ran these maps")
      .get()
      .addOnSuccessListener { result ->
        var sumDistance = 0.0
        var sumTime = 0.0

        for (document in result) {
          sumDistance += document.get("distance") as Double
          sumTime += document.get("time") as Long
        }
        // 구하고 나서 이용자가 만든 맵의 거리와 시간을 더함
        db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname)
          .get()
          .addOnSuccessListener { result2 ->
            for (document2 in result2) {

              sumDistance += document2.get("distance") as Double
              sumTime += document2.get("time") as Long
            }

            // 총 거리와 시간을 띄워줌
            view.profileFragmentTotalDistance.text = PrettyDistance().convertPretty(sumDistance)
            val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
            view.profileFragmentTotalTime.text = formatter.format(Date(sumTime.toLong()))

          }
      }

    // storage 에 올린 경로를 db에 저장해두었으니 다시 역 추적 하여 프로필 이미지 반영
    db.collection("userinfo").whereEqualTo("nickname", UserInfo.nickname)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          profileImagePath = document.get("profileImagePath") as String
        }
        // glide imageview 소스
        // 프사 설정하는 코드 db -> imageView glide
        val imageView = view.findViewById<ImageView>(R.id.profileImageView)

        val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
        val profileRef = storage.reference.child("Profile").child(UserInfo.autoLoginKey).child(profileImagePath)

        profileRef.downloadUrl.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            // Glide 이용하여 이미지뷰에 로딩
            Glide.with(view)
              .load(task.result)
              .override(1024, 980)
              .into(imageView)
            progressbar.dismiss()
          } else {
          }
        }
      }
      .addOnFailureListener { exception ->
      }
  }

  fun getMyRoute(activity: Activity) {
    /*val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()*/
    val infoData: ArrayList<InfoData> = arrayListOf()

    val db = FirebaseFirestore.getInstance()

    db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname).whereEqualTo("privacy", "RACING")
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val data = document.toObject(InfoData::class.java)
          infoData.add(data)
        }

        if (result.isEmpty) {
          activity.profileRecyclerRouteisEmpty.visibility = View.VISIBLE
        } else {
          activity.profileRecyclerRouteisEmpty.visibility = View.GONE
        }

        //adpater 추가
        activity.profileRecyclerRoute.adapter = ProfileRecyclerViewAdapterRoute(infoData)
        activity.profileRecyclerRoute.layoutManager = LinearLayoutManager(activity)
        //  progressbar.dismiss()
      }
      .addOnFailureListener { exception ->
      }
  }
}