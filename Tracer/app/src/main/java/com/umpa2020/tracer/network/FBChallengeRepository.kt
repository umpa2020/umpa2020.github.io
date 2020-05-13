package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.BannerData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

/**
 *
 */

class FBChallengeRepository : BaseFB() {

  /**
   *
   */
  fun createChallengeData(challengeData: ChallengeData) {
    db.collection(CHALLENGES).add(challengeData)
  }

  suspend fun getChallengeData(challengeId: String): ChallengeData {
    return db.collection(CHALLENGES)
      .whereEqualTo(ID, challengeId)
      .get()
      .await()
      .documents.last().toObject(ChallengeData::class.java)!!
  }

  /**
   * 복합 색인 되면 이걸로
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
   * 바로 imageView 를 가져와서 등록한다.
   */

  suspend fun listChallengeBannerImagePath(): MutableList<BannerData>? {
    return db.collection(CHALLENGE_BANNERS).get().await()
      .documents.map { it.toObject(BannerData::class.java)!! }.toMutableList()
  }
}

