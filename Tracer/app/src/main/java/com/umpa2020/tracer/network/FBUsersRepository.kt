package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.network.BaseFB.Companion.ACTIVITIES
import com.umpa2020.tracer.network.BaseFB.Companion.USERS
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ID
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

/**
 * 유저 히스토리 관련 클래스
 * 사용법 - FBUserHistoryRepository().관련기능함수()
 *
 * 1. 유저가 뛴 맵 history 저장
 * 2. 유저가 만든 맵 history 저장
 * 3. 유저가 challenge 한 맵 history 저장
 */

class FBUsersRepository {
  val db = FirebaseFirestore.getInstance()
  var globalStartAfter: DocumentSnapshot? = null

  fun createUserInfo(data: HashMap<String, String?>) {
    db.collection(USERS).document(data[USER_ID]!!).set(data)
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully written!") }
      .addOnFailureListener { e -> Logg.w("Error writing document$e") }
  }

  fun createUserHistory(activityData: ActivityData) {
    db.collection(USERS).whereEqualTo(USER_ID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(ACTIVITIES).add(activityData)
      }
  }

  suspend fun listUserMakingActivity(limit: Long): List<ActivityData>? {
    return (if (globalStartAfter == null) db.collection(USERS).document(UserInfo.autoLoginKey).collection(ACTIVITIES)
    else db.collection(USERS).document(UserInfo.autoLoginKey).collection(ACTIVITIES).startAfter(globalStartAfter!!))
      .limit(limit).get().await().apply {
        globalStartAfter = last()
      }.toObjects(ActivityData::class.java)
  }

  suspend fun listUserRoute(uid: String, limit: Long): List<InfoData>? {
    val infoDatas =
      if (globalStartAfter == null) {
        db.collection(BaseFB.MAPS).whereEqualTo(BaseFB.MAKER_ID, uid)
      } else {
        db.collection(BaseFB.MAPS).whereEqualTo(BaseFB.MAKER_ID, uid).startAfter(globalStartAfter!!)
      }.limit(limit).get().await().apply {
        if (documents.size == 0)
          return null
        globalStartAfter = documents.last()
      }.toObjects(InfoData::class.java)


    val playedMapIdList = FBMapRepository().listPlayed()
    val likedMapIdList = FBMapRepository().listLikedMap()
    infoDatas.filter { playedMapIdList.contains(it.mapId) }.forEach { it.played = true }
    infoDatas.filter { likedMapIdList.contains(it.mapId) }.forEach { it.liked = true }
    return infoDatas
  }
}