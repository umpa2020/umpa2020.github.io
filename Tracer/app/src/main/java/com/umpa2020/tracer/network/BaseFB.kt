package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * 파이어베이스 통신 기본 통신 규약
 *
 * 상수 같은 내용이나 Instance 를 담고 있어
 * 이 클래스를 상속 받아 사용한다.
 */

open class BaseFB {
  val db = FirebaseFirestore.getInstance()
  val storage = FirebaseStorage.getInstance()
  val usersCollectionRef = db.collection(USERS)
  val mapsCollectionRef = db.collection(MAPS)
  val achievementCollectionRef = db.collection(ACHIEVEMENT)
  val mapRouteStorageRef = storage.reference.child(MAP_ROUTE)

  enum class ActivityMode {
    MAP_SAVE,RACING_SUCCESS,RACING_FAIL
  }

  companion object {
    /**
     * mapId 가 기본키 mapTitle + timeStamp = mapId
     */
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
    const val USER_STATE = "userState"

    const val TIME = "time"
    const val PLAY_TIME = "playTime"
    const val MAKER_ID = "makerId"

    const val CHALLENGER_Id = "challengerId"

    // for profile
    const val PROFILE_IMAGE_PATH = "profileImagePath"
    const val PROFILE = "Profile"

    const val NICKNAME = "nickname"

    // for racing
    const val MAP_ROUTE = "mapRoute"
    const val RACING_GPX = "racingGPX"

    // for challenge
    const val CHALLENGE_BANNERS = "challengeBanners"
    const val CHALLENGES = "challenges"
    const val ID = "id"
    const val DATE = "date"
    const val LOCALE = "locale"

    const val ACHIEVEMENT = "achievement"
    const val TROPHIES = "trophies"
    const val MODE = "mode"
    const val TRACK_MAKE = "trackMake"
  }
}