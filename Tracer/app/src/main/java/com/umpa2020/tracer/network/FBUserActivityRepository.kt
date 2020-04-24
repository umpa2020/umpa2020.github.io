package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.ActivityData
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
  var activityDatas = arrayListOf<ActivityData>()



  fun setUserHistory(activityData: ActivityData) {
    db.collection("userinfo").whereEqualTo("UID", UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("user activity").add(activityData)
      }
  }

  fun getUserMakingActivityFirst(activityListener: ActivityListener, limit: Long) {

    Logg.d("ssmm11 limit = $limit")
    db.collection("userinfo").whereEqualTo("UID", UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("user activity")
          .limit(limit)
          .get()
          .addOnSuccessListener {
            it.documents.forEach { result ->
              val activityData = result.toObject(ActivityData::class.java)
              activityDatas.add(activityData!!)
              globalStartAfter = result
            }

            activityListener.activityList(activityDatas)
          }
      }
  }

  fun getUserMakingActivity(activityListener: ActivityListener, limit: Long) {
    val activityDatas = arrayListOf<ActivityData>()

    Logg.d("ssmm11 limit = $limit")
    Logg.d("ssmm11 global = $globalStartAfter")


    db.collection("userinfo").whereEqualTo("UID", UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("user activity")
          .startAfter(globalStartAfter)
          .limit(limit)
          .get()
          .addOnSuccessListener {
            it.documents.forEach { result ->
              val activityData = result.toObject(ActivityData::class.java)
              activityDatas.add(activityData!!)
              globalStartAfter = result
            }

            activityListener.activityList(activityDatas)
          }
      }
  }
}