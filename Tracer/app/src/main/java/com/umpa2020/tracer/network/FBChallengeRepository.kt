package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentReference
import com.umpa2020.tracer.dataClass.BannerData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.ChallengeRecordData
import kotlinx.coroutines.tasks.await

/**
 * Challenge Tab 전용 Repository
 */

class FBChallengeRepository : BaseFB() {

  /**
   * Challenge Mode 데이터 추가하는 함수
   * 현재 Challenge Tool bar 누르면 실행
   * Release 에서는 닫아놓을 것
   */

  fun createChallengeData(challengeData: ChallengeData) {
    db.collection(CHALLENGES).document(challengeData.id!!).set(challengeData)
  }

  /**
   * Challenge Mode 에서 하나의 챌린지가 선택되면
   * Challenge ID 를 통해서 하나의 챌린지 데이터를 받아오는 함수
   */

  suspend fun getChallengeData(challengeId: String): ChallengeData {
    return db.collection(CHALLENGES)
      .whereEqualTo(ID, challengeId)
      .get()
      .await()
      .documents.last().toObject(ChallengeData::class.java)!!
  }

  /**
   * Challenge Data 를 list 형태로 받아오는 함수
   * 주로 필터링의 결과 값을 가져오는 데 사용
   */

  suspend fun listChallengeData(fromDate: Long, toDate: Long, region: String): MutableList<ChallengeData>? {
    return if (region == "전국") {
      db.collection(CHALLENGES)
        .whereGreaterThan(DATE, fromDate)
        .whereLessThan(DATE, toDate)
        .get()
        .await().documents.map {
          it.toObject(ChallengeData::class.java)!!
        }.toMutableList()
    } else {
      db.collection(CHALLENGES)
        .whereGreaterThan(DATE, fromDate)
        .whereLessThan(DATE, toDate)
        .whereArrayContains(LOCALE, region)
        .get()
        .await().documents.map {
          it.toObject(ChallengeData::class.java)!!
        }.toMutableList()
    }
  }

  /**
   * Challenge mode 첫 화면 배너들의 이미지 Path 를 전부 가져오는 함수
   * 바로 imageView 를 가져와서 등록한다.
   */

  suspend fun listChallengeBannerImagePath(): MutableList<BannerData>? {
    return db.collection(CHALLENGE_BANNERS).get().await()
      .documents.map { it.toObject(BannerData::class.java)!! }.toMutableList()
  }

  suspend fun createChallengeRecord(challengeId: String, challengeRecordData: ChallengeRecordData): DocumentReference? {
    return db.collection(CHALLENGES).document(challengeId).collection(RANKING).add(challengeRecordData).await()
  }

  suspend fun getChallengeRecord(challengeRecordReference: DocumentReference): ChallengeRecordData? {
    return challengeRecordReference.get().await().toObject(ChallengeRecordData::class.java)
  }
}

