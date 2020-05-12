package com.umpa2020.tracer.network

import android.net.Uri
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.dataClass.*
import com.umpa2020.tracer.extensions.classToGpx
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await
import java.io.File
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
    racerData: InfoData,
    rankingData:RankingData,
    racerGpxFile:Uri
  ) {
    val timestamp = Date().time
    //besttime인지 체크
    db.collection(MAPS).document(racerData.mapId!!).collection(RANKING)
      .whereEqualTo(BEST_TIME, true)
      .get().await().let {
        it.documents.filter {
          it.get(CHALLENGER_Id) == UserInfo.nickname &&
          it.getLong(CHALLENGER_TIME)!! > racerData.time!!.toLong()}.forEach {
            it.reference.update(BEST_TIME, false)
          rankingData.BestTime = false
          }
      }
    rankingData.racerGPX = "$MAP_ROUTE/${racerData.mapId}/$RACING_GPX/$UserInfo.autoLoginKey"
    db.collection(MAPS).document(racerData.mapId!!).collection(RANKING)
      .document(UserInfo.autoLoginKey + timestamp).set(rankingData)

    //upload racerGpxFile
    FBStorageRepository().uploadFile(racerGpxFile, rankingData.racerGPX!!)
  }


  /**
   * 다른 사람 인포데이터를 가져오는 함수
   */
  fun getOtherData(mapTitle: String, nickname: String, racingFinishListener: RacingFinishListener) {

    db.collection(MAPS).document(mapTitle)
      .collection(RANKING).whereEqualTo(CHALLENGER_Id, nickname)
      .whereEqualTo(BEST_TIME, true)
      .get()
      .addOnSuccessListener {
        val rankingData = it.documents.last().toObject(RankingData::class.java)
        racingFinishListener.getOtherRacing(rankingData!!)
      }
  }

  /**
   * 유저 인포에 해당 유저가 이 맵을 뛰었다는
   * 히스토리를 더하는 함수
   */
  fun createUserInfoRacing(racerData: InfoData) {
    val activityData = ActivityData(racerData.mapId, racerData.time, racerData.distance, "racing go the distance")
    db.collection(USERS).document(UserInfo.autoLoginKey).collection(ACTIVITIES)
      .add(activityData)
  }

  fun listRacingGPX(mapTitle: String, racerIdList: Array<String>, listener: RacingListener) {
    val racerGPXList = mutableListOf<RouteGPX>()
    var count = 0
    racerIdList.forEach {
      val routeRef = mapRouteStorageRef.child(mapTitle).child(RACING_GPX).child(it)
      val localFile = File.createTempFile("routeGpx", "xml")
      routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
        racerGPXList.add(localFile.path.gpxToClass())
        count++
        if (racerGPXList.size == racerIdList.size) {
          listener.racingList(racerGPXList.toTypedArray())
        }
      }
    }
  }
}