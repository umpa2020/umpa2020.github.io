package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.network.BaseFB.Companion.UID
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ACTIVITY
import com.umpa2020.tracer.network.BaseFB.Companion.USER_INFO
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

  fun createUserHistory(activityData: ActivityData) {
    db.collection(USER_INFO).whereEqualTo(UID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(USER_ACTIVITY).add(activityData)
      }
  }

  fun listUserMakingActivity(activityListener: ActivityListener) {
    val activityDatas = arrayListOf<ActivityData>()

    db.collection(USER_INFO).whereEqualTo(UID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(USER_ACTIVITY).get()
          .addOnSuccessListener {
            it.documents.forEach { result ->
              val activityData = result.toObject(ActivityData::class.java)
              activityDatas.add(activityData!!)
            }
            activityListener.activityList(activityDatas)
          }
      }
  }
}