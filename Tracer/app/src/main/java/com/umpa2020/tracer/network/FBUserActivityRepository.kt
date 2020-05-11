package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ID
import com.umpa2020.tracer.network.BaseFB.Companion.ACTIVITIES
import com.umpa2020.tracer.network.BaseFB.Companion.USERS
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo

/**
 * 유저 히스토리 관련 클래스
 * 사용법 - FBUserHistoryRepository().관련기능함수()
 *
 * 1. 유저가 뛴 맵 history 저장
 * 2. 유저가 만든 맵 history 저장
 * 3. 유저가 challenge 한 맵 history 저장
 */

class FBUserActivityRepository {
  val db = FirebaseFirestore.getInstance()
  lateinit var globalStartAfter: DocumentSnapshot


  fun createUserHistory(activityData: ActivityData) {
    db.collection(USERS).whereEqualTo(USER_ID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(ACTIVITIES).add(activityData)
      }
  }

  fun listUserMakingActivityFirst(activityListener: ActivityListener, limit: Long) {

    Logg.d("ssmm11 limit = $limit")
    db.collection(USERS).whereEqualTo(USER_ID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(ACTIVITIES)
          .limit(limit)
          .get()
          .addOnSuccessListener {
            activityListener.activityList(ArrayList(it.documents.map {
              globalStartAfter = it
              it.toObject(ActivityData::class.java)!!
            }))
          }
      }
  }

  fun listUserMakingActivity(activityListener: ActivityListener, limit: Long) {

    db.collection(USERS).whereEqualTo(USER_ID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener { it ->
        it.documents.last().reference.collection(ACTIVITIES)
          .startAfter(globalStartAfter)
          .limit(limit)
          .get()
          .addOnSuccessListener {
            activityListener.activityList(ArrayList(it.documents.map {
              globalStartAfter = it
              it.toObject(ActivityData::class.java)!!
            }))
          }
      }
  }
}