package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.LikeMapsData
import com.umpa2020.tracer.util.UserInfo

class Likes {
  val db = FirebaseFirestore.getInstance()

  fun getLikes(): ArrayList<LikeMapsData> {
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps")
      .get()
      .addOnSuccessListener { result ->
        val likeMapsDatas = arrayListOf<LikeMapsData>()

        for (document in result) {
          val likeMapsData = LikeMapsData(document.get("mapTitle") as String, document.get("uid") as String)
          likeMapsDatas.add(likeMapsData)
        }
        //TODO: 핸들러로 구현 필요
        //return likeMapsDatas

      }
  }

  fun setLikes(maptitle: String) {

    val likeMapsData = LikeMapsData(maptitle, UserInfo.autoLoginKey)
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps").add(likeMapsData)
    db.collection("mapInfo").document(maptitle).collection("likes").add(likeMapsData)
  }
}