package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore

open class BaseFB {
  val db = FirebaseFirestore.getInstance()
  val userInfoColRef = db.collection(USER_INFO)
  val mapInfoColRef = db.collection(MAP_INFO)

  companion object {
    const val MAP_TITLE = "mapTitle"

    const val USER_INFO = "userinfo"


    const val USER_LIKED_THESE_MAPS = "user liked these maps"
    const val LIKES = "likes"

    const val RANKING_MAP = "rankingMap"
    const val BEST_TIME = "bestTime"
    const val CHALLENGER_TIME = "challengerTime"
    const val RANKING ="ranking"

    const val MAP_INFO = "mapInfo"
    const val START_LATITUDE = "startLatitude"
    const val START_LONGITUDE = "startLongitude"
    const val DISTANCE = "distance"

    const val EXECUTE = "execute"
    const val USER_ACTIVITY = "user activity"
    const val UID = "UID"
    const val MAP_IMAGE = "mapImage"

    const val USER_RAN_THESE_MAPS = "user ran these maps"

    const val TIME = "time"
    const val MAKERS_NICKNAME = "makersNickname"

    const val PROFILE_IMAGE_PATH = "profileImagePath"
    const val PROFILE = "Profile"

    const val NICKNAME = "nickname"

    const val PRIVACY = "privacy"
    const val RACING = "RACING"

  }
}