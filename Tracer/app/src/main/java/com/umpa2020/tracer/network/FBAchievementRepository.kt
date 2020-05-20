package com.umpa2020.tracer.network

import com.google.firebase.firestore.FieldValue
import com.umpa2020.tracer.dataClass.AchievementData
import com.umpa2020.tracer.dataClass.EmblemData
import com.umpa2020.tracer.dataClass.EmblemNameData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FBAchievementRepository : BaseFB(), CoroutineScope by MainScope() {
  /**
   * 회원가입 시 초기화 생성
   */
  fun createAchievement(userId: String) {
    val achievementData = AchievementData(0.0, 0, 0)
    achievementCollectionRef.document(userId).set(achievementData)
  }

  /**
   * 업적 가져오기
   */

  suspend fun getAchievement(userId: String): AchievementData? {
    return achievementCollectionRef.document(userId).get().await().toObject(AchievementData::class.java)
  }

  /**
   * 개인이 보유한 업적 가져오기
   */

  suspend fun listUserEmblemNames(userId: String): MutableList<EmblemNameData> {
    return usersCollectionRef.document(userId).collection(EMBLEMS).get().await().documents.map {
      it.toObject(EmblemNameData::class.java)!!
    }.toMutableList()
  }

  suspend fun listEmblemImagePaths(userEmblems: MutableList<EmblemNameData>): MutableList<EmblemData> {
    return emblemsCollectionRef.orderBy(NO).get().await().documents.map {
      val emblemNameData = EmblemNameData(it.getString(NAME)!!)
      if (userEmblems.contains(emblemNameData)) {
        EmblemData(it.getString(NAME), it.getString(IMAGE_PATH))
      } else {
        EmblemData(it.getString(NAME), it.getString(IMAGE_PATH_BLACK))
      }
    }.toMutableList()
  }

  /**
   * 증가 계열
   */

  fun incrementTrackMake(userId: String) {
    achievementCollectionRef.document(userId).update(TRACK_MAKE, FieldValue.increment(1))
  }

  fun incrementDistance(userId: String, distance: Double) {
    achievementCollectionRef.document(userId).update(DISTANCE, FieldValue.increment(distance))
      .addOnSuccessListener {
        launch {
          getAchievement(userId).let {
            when {
              it!!.distance > DISTANCE_COUNT_10 -> {
                setDistance10Emblem(userId)
              }
              it.distance > DISTANCE_COUNT_50 -> {
                setDistance50Emblem(userId)
              }
              it.distance > DISTANCE_COUNT_100 -> {
                setDistance100Emblem(userId)
              }
            }
          }
        }
      }
  }

  fun incrementPlays(userId: String) {
    launch {
      getAchievement(userId).let {
        when {
          it!!.plays == TIMES_OF_RUN_COUNT_9 -> {
            setTimesOfRun10Emblem(userId)
          }
          it.plays == TIMES_OF_RUN_COUNT_99 -> {
            setTimesOfRun100Emblem(userId)
          }
          it.plays == TIMES_OF_RUN_COUNT_999 -> {
            setTimesOfRun1000Emblem(userId)
          }
        }
        achievementCollectionRef.document(userId).update(PLAYS, FieldValue.increment(1))
      }
    }
  }

  /**
   * 엠블럼 셋팅 계열
   */

  fun setTrackMaker1Emblem(userId: String) {
    val emblemNameData = EmblemNameData(TRACK_MAKER_1)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setTrackMaker10Emblem(userId: String) {
    val emblemNameData = EmblemNameData(TRACK_MAKER_10)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setTrackMaker50Emblem(userId: String) {
    val emblemNameData = EmblemNameData(TRACK_MAKER_50)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setDistance10Emblem(userId: String) {
    val emblemNameData = EmblemNameData(DISTANCE_10)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setDistance50Emblem(userId: String) {
    val emblemNameData = EmblemNameData(DISTANCE_50)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setDistance100Emblem(userId: String) {
    val emblemNameData = EmblemNameData(DISTANCE_100)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setTimesOfRun10Emblem(userId: String) {
    val emblemNameData = EmblemNameData(TIMES_OF_RUN_10)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setTimesOfRun100Emblem(userId: String) {
    val emblemNameData = EmblemNameData(TIMES_OF_RUN_100)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }

  fun setTimesOfRun1000Emblem(userId: String) {
    val emblemNameData = EmblemNameData(TIMES_OF_RUN_1000)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemNameData)
  }
}