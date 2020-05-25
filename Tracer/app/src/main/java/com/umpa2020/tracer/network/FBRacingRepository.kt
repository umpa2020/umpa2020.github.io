package com.umpa2020.tracer.network

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.fileDelete
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await
import java.util.*

class FBRacingRepository : BaseFB() {

  /**
   * 첫 번째 실행 되어야하는 함수
   * 현재 유저와 다른 레이서들의 기록을 비교하여
   * 최고 기록일 경우 bestTime을 1로 설정하고
   * 최고 기록이 아닐 경우 bestTime을 0으로 설정하여
   * ranking 에 등록하는 함수
   */
  suspend fun createRankingData(
    racerData: MapInfo,
    rankingData: RankingData,
    racerGpxFile: Uri
  ): DocumentReference {
    val timestamp = Date().time
    //besttime인지 체크
    db.collection(MAPS).document(racerData.mapId).collection(RANKING)
      .whereEqualTo(BEST_TIME, true)
      .whereEqualTo(CHALLENGER_Id, UserInfo.autoLoginKey)
      .get().await().let {
        it.documents.filter {
          it.getLong(CHALLENGER_TIME)!! > racerData.time
        }.forEach {
          it.reference.update(BEST_TIME, false)
          rankingData.BestTime = true
        }
        if (it.isEmpty) {
          rankingData.BestTime = true
        }
      }
    rankingData.racerGPX = "$MAP_ROUTE/${racerData.mapId}/$RACING_GPX/${UserInfo.autoLoginKey}"
    db.collection(MAPS).document(racerData.mapId).collection(RANKING)
      .document(UserInfo.autoLoginKey + timestamp).set(rankingData)

    //upload racerGpxFile
    FBStorageRepository().uploadFile(racerGpxFile, rankingData.racerGPX!!).fileDelete()
    return db.collection(MAPS).document(racerData.mapId).collection(RANKING)
      .document(UserInfo.autoLoginKey + timestamp)
  }


  suspend fun listRacingGPX(mapId: String, racerIdList: List<String>): List<RouteGPX> {
    return racerIdList.map {
      FBStorageRepository().getFile("$MAP_ROUTE/$mapId/$RACING_GPX/$it").gpxToClass()
    }
  }

  suspend fun getOtherData(mapId: String, userId: String): RankingData {
    return mapsCollectionRef.document(mapId).collection(RANKING).whereEqualTo(CHALLENGER_Id, userId).whereEqualTo(BEST_TIME, true)
      .get().await().documents.first().toObject(RankingData::class.java)!!
  }

  suspend fun getRankingData(rankingData: DocumentReference): RankingData {
    return rankingData.get().await().toObject(RankingData::class.java)!!
  }
}
