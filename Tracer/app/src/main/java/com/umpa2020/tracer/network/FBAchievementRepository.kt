package com.umpa2020.tracer.network

import com.google.firebase.firestore.FieldValue
import com.umpa2020.tracer.dataClass.AchievementData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.EmblemData
import kotlinx.coroutines.tasks.await

class FBAchievementRepository : BaseFB() {
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

  suspend fun listUserEmblems(userId: String): MutableList<EmblemData> {
    return usersCollectionRef.document(userId).collection(EMBLEMS).get().await().documents.map {
      it.toObject(EmblemData::class.java)!!
    }.toMutableList()
  }

  suspend fun listEmblemImagePaths(userEmblems: MutableList<EmblemData>): MutableList<String> {
    return emblemsCollectionRef.orderBy(NO).get().await().documents.map {
      val emblemData = EmblemData(it.getString(NAME)!!)
      if (userEmblems.contains(emblemData)) {
        it.getString(IMAGE_PATH)!!
      } else {
        it.getString(IMAGE_PATH_BLACK)!!
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
  }

  fun incrementPlays(userId: String) {
    achievementCollectionRef.document(userId).update(PLAYS, FieldValue.increment(1))
  }

  /**
   * 엠블럼 셋팅 계열
   */

  fun setTrackMaker1Emblem(userId: String) {
    val emblemData = EmblemData(TRACK_MAKER_1)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setTrackMaker10Emblem(userId: String) {
    val emblemData = EmblemData(TRACK_MAKER_10)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setTrackMaker50Emblem(userId: String) {
    val emblemData = EmblemData(TRACK_MAKER_50)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setDistance10Emblem(userId: String) {
    val emblemData = EmblemData(DISTANCE_10)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setDistance50Emblem(userId: String) {
    val emblemData = EmblemData(DISTANCE_50)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setDistance100Emblem(userId: String) {
    val emblemData = EmblemData(DISTANCE_100)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setTimesOfRun10Emblem(userId: String) {
    val emblemData = EmblemData(TIMES_OF_RUN_10)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setTimesOfRun100Emblem(userId: String) {
    val emblemData = EmblemData(TIMES_OF_RUN_100)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }

  fun setTimesOfRun1000Emblem(userId: String) {
    val emblemData = EmblemData(TIMES_OF_RUN_1000)
    usersCollectionRef.document(userId).collection(EMBLEMS).add(emblemData)
  }
}