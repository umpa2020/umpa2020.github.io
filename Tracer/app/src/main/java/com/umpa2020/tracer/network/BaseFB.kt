package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

open class BaseFB {
  val db = FirebaseFirestore.getInstance()
  val storage = FirebaseStorage.getInstance()
  val userInfoColRef = db.collection(USERS)
  val mapInfoColRef = db.collection(MAPS)
  val mapRouteStorageRef = storage.reference.child(MAP_ROUTE)

  companion object {
    const val MAP_ID = "mapId"
    const val MAP_TITLE = "mapTitle"

    const val USERS = "users"


    const val LIKED_MAP = "likedMap"
    const val LIKES = "likes"

    const val BEST_TIME = "bestTime"
    const val CHALLENGER_TIME = "challengerTime"
    const val RANKING = "ranking"

    const val MAPS = "maps"

    const val START_LATITUDE = "startLatitude"
    const val START_LONGITUDE = "startLongitude"
    const val DISTANCE = "distance"

    const val PLAYS = "plays"
    const val ACTIVITIES = "activities"
    const val USER_ID = "userId"
    const val MAP_IMAGE_PATH = "mapImagePath"

    const val TIME = "time"
    const val MAKER_ID = "makerId"

    const val CHALLENGER_Id = "challengerId"

    // for profile
    const val PROFILE_IMAGE_PATH = "profileImagePath"
    const val PROFILE = "Profile"

    const val NICKNAME = "nickname"

    const val PRIVACY = "privacy"
    const val RACING = "RACING"


    // for racing
    const val MAP_ROUTE = "mapRoute"
    const val RACING_GPX = "racingGPX"

    // for challenge
    const val CHALLENGE_BANNERS = "challengeBanners"
    const val CHALLENGES = "challenges"
    const val ID = "id"
    const val DATE = "date"
    const val LOCALE = "locale"
  }
}