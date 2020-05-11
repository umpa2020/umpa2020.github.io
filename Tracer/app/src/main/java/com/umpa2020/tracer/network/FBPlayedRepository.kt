package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.PlayedMapData
import com.umpa2020.tracer.network.BaseFB.Companion.ACTIVITIES
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ID
import com.umpa2020.tracer.network.BaseFB.Companion.USERS
import com.umpa2020.tracer.util.UserInfo

/**
 * 내가 뛰었던 맵 가져오는 Repository
 */

class FBPlayedRepository : BaseFB() {
  val playedMapDatas = arrayListOf<PlayedMapData>()

  fun listPlayed(playedMapListener: PlayedMapListener) {
    db.collection(USERS).whereEqualTo(USER_ID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.last().reference.collection(ACTIVITIES)
          .get().addOnSuccessListener { documents ->
            documents.forEach {
              val playedMapData = PlayedMapData(it.get(MAP_ID) as String, UserInfo.autoLoginKey)
              playedMapDatas.add(playedMapData)
            }
            playedMapListener.played(playedMapDatas)
          }
      }
  }
}