package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore

open class BaseFB{
  val db = FirebaseFirestore.getInstance()
  val userInfoColRef=db.collection("userinfo")
  val mapInfoColRef=db.collection("mapInfo")

  companion object {
    const val USER_LIKED_THESE_MAPS="user liked these maps"
    const val MAP_TITLE="mapTitle"
  }
}