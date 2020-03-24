package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.LikeMapsData
import com.umpa2020.tracer.util.UserInfo

class Likes {
  val GETLIKES = 50
  val db = FirebaseFirestore.getInstance()

  fun getLikes(mHandler: Handler) {
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps")
      .get()
      .addOnSuccessListener { result ->
        val msg: Message

        val likeMapsDatas = arrayListOf<LikeMapsData>()

        for (document in result) {
          val likeMapsData = LikeMapsData(document.get("mapTitle") as String, document.get("uid") as String)
          likeMapsDatas.add(likeMapsData)
        }
        msg = mHandler.obtainMessage(GETLIKES)
        msg.obj = likeMapsDatas
        mHandler.sendMessage(msg)
      }
  }

  fun setLikes(maptitle: String) {

    val likeMapsData = LikeMapsData(maptitle, UserInfo.autoLoginKey)
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps").add(likeMapsData)
    db.collection("mapInfo").document(maptitle).collection("likes").add(likeMapsData)
  }
}