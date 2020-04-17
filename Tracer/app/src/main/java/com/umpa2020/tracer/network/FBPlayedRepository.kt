package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.PlayedMapData
import com.umpa2020.tracer.util.UserInfo

class FBPlayedRepository {
  val db = FirebaseFirestore.getInstance()
  val playedMapDatas = arrayListOf<PlayedMapData>()

  fun getPlayed(playedMapListener: PlayedMapListener) {

    db.collection("userinfo").whereEqualTo("UID", UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.last().reference.collection("user ran these maps")
          .get().addOnSuccessListener { documents ->
            documents.forEach {
              val playedMapData = PlayedMapData(it.get("mapTitle") as String, UserInfo.autoLoginKey)
              playedMapDatas.add(playedMapData)
            }
            playedMapListener.played(playedMapDatas)
          }
      }

  }
}