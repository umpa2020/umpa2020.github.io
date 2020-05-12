package com.umpa2020.tracer.network

import com.google.firebase.firestore.Query
import com.umpa2020.tracer.dataClass.RankingData
import kotlinx.coroutines.tasks.await

/**
 * 한 맵에 대한 랭킹만 가져오는
 * Repository
 */

class FBMapRankingRepository : BaseFB() {
  suspend fun listMapRanking(mapId: String): MutableList<RankingData> {
    // 베스트 타임이 랭킹 가지고 있는 것 중에서 이것이 베스트 타임인가를 나타내주는 1,0 값입니다.
    // 그래서 한 사용자의 베스트 타임만 가져오고 또 그것들 중에서 오름차순해서 순위 나타냄

    return db.collection(MAPS).document(mapId).collection(RANKING)
      .whereEqualTo(BEST_TIME, true)
      .orderBy(CHALLENGER_TIME, Query.Direction.ASCENDING)
      .get()
      .await().toObjects(RankingData::class.java)
  }
}