package com.umpa2020.tracer.network

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.TrophyData
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

/**
 * 맵에 대한 Repository
 */

class FBMapRepository : BaseFB() {

  /**
   * mapID 하나를 받으면 맵 인포 데이터를 받아오는 함수.
   */
  suspend fun getMapInfo(mapId: String): MapInfo? {
    return mapsCollectionRef
      .whereEqualTo(MAP_ID, mapId)
      .get()
      .await().documents.first().toObject(MapInfo::class.java)
  }

  /**
   * mapID를 받으면 mapTitle 을 반환하는 함수.
   */
  suspend fun getMapTitle(mapId: String): String? {
    return mapsCollectionRef
      .whereEqualTo(MAP_ID, mapId)
      .get()
      .await().documents.first().getString(MAP_TITLE)
  }

  /**
   * mapID를 받으면 맵 이미지를 받아오는 함수.
   */
  suspend fun getMapImage(mapId: String): Uri? {
    return mapsCollectionRef.document(mapId).get().await().let {
      FBStorageRepository().downloadFile(it.getString(MAP_IMAGE_PATH)!!)
    }
  }

  /**
   * mapId의 plays를 반환
   */
  suspend fun getMapPlays(mapId: String): Int {
    return mapsCollectionRef.document(mapId).get().await().getLong(PLAYS)!!.toInt()
  }

  /**
   * StartFragment 에서 현재 화면에 보이는 지도 안에서
   * Tracer 에서 만들어진 맵이 있다면 찾아와서 마커에 표현할 수 있도록
   * 리스트로 받아오는 함수
   */
  suspend fun listNearMap(southwest: LatLng, northeast: LatLng): List<MapInfo> {
    return mapsCollectionRef
      .whereGreaterThan(START_LATITUDE, southwest.latitude)
      .whereLessThan(START_LATITUDE, northeast.latitude)
      .get().await().documents.filter {
        val startLongitude = it.getDouble(START_LONGITUDE)!!
        ((southwest.longitude > 0 && northeast.longitude < 0) &&
          (southwest.longitude < startLongitude || startLongitude < northeast.longitude))
          || (southwest.longitude < startLongitude && startLongitude < northeast.longitude)
      }.map { it.toObject(MapInfo::class.java)!! }
  }

  /**
   * 맵을 실행하면 db에 실행수를 1 증가시켜주는 함수
   */
  fun incrementExecute(mapTitle: String) {
    mapsCollectionRef.document(mapTitle).update(PLAYS, FieldValue.increment(1))
  }

  /**
   * 러닝을 하고 맵 세이브를 하면 db에 맵을 업로드
   *
   * 1. maps 컬렉션에 map infoData 를 업로드
   * 2. image와 GPX 파일을 Storage에 업로드
   * 3. ranking 에 맵 제작자의 ranking 을 업로드
   * 4. users activity 에 map save 로 해당 내용 저장
   */
  fun uploadMap(mapInfo: MapInfo, rankingData: RankingData, activityData: ActivityData, timestamp: String, gpxUri: Uri, imgPath: Uri, trophyData: TrophyData) {
    //Maps/mapId에 새로운 맵 정보 생성
    mapsCollectionRef.document(mapInfo.mapId).set(mapInfo)
    //racerGPX
    FBStorageRepository().uploadFile(imgPath, mapInfo.mapImagePath)
    FBStorageRepository().uploadFile(gpxUri, mapInfo.routeGPXPath)
    FBStorageRepository().uploadFile(gpxUri, rankingData.racerGPX!!)
    mapsCollectionRef.document(mapInfo.mapId).collection(RANKING)
      .document(UserInfo.autoLoginKey + timestamp).set(rankingData)
    // 히스토리 업로드
    FBUsersRepository().createUserHistory(activityData)
    FBUsersRepository().createUserAchievement(trophyData)

  }

  /**
   * 내가 실행했던 맵을 id로 체크해주는 함수
   */
  suspend fun listPlayed(): List<String> {
    return usersCollectionRef.document(UserInfo.autoLoginKey).collection(ACTIVITIES)
      .get()
      .await()
      .documents.map {
        it.getString(MAP_ID)!!
      }
  }

  /**
   * 내가 좋아요했던 맵을 id로 체크해주는 함수
   */
  suspend fun listLikedMap(): List<String> {
    return usersCollectionRef.document(UserInfo.autoLoginKey).collection(LIKED_MAP)
      .get().await().documents.map {
        it.getString(MAP_ID)!!
      }
  }

  /**
   * 베스트 타임이 랭킹 가지고 있는 것 중에서 이것이 베스트 타임인가를 나타내주는 1,0 값입니다.
   * 그래서 한 사용자의 베스트 타임만 가져오고 또 그것들 중에서 오름차순해서 순위 나타냄
   */
  suspend fun listMapRanking(mapId: String): MutableList<RankingData> {
    return mapsCollectionRef.document(mapId).collection(RANKING)
      .whereEqualTo(BEST_TIME, true)
      .orderBy(CHALLENGER_TIME, Query.Direction.ASCENDING)
      .get()
      .await().toObjects(RankingData::class.java)
  }
}