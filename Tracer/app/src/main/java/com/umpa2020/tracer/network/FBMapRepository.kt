package com.umpa2020.tracer.network

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

class FBMapRepository : BaseFB() {
  val NEARMAPFALSE = 41
  val STRAT_FRAGMENT_NEARMAP = 30
  private var nearMaps: ArrayList<NearMap> = arrayListOf()

  suspend fun getMapInfo(mapId: String): InfoData? {
    return db.collection(MAPS)
      .whereEqualTo(MAP_ID, mapId)
      .get()
      .await().documents.first().toObject(InfoData::class.java)
  }

  suspend fun getMapTitle(mapId: String): String? {
    return db.collection(MAPS)
      .whereEqualTo(MAP_ID, mapId)
      .get()
      .await().documents.first().getString(MAP_TITLE)
  }

  suspend fun getMapImage(mapId: String): Uri? {
    return db.collection(MAPS).document(mapId).get().await().let {
      FBStorageRepository().downloadFile(it.getString(MAP_IMAGE_PATH)!!)
    }
  }

  suspend fun listNearMap(southwest: LatLng, northeast: LatLng): List<NearMap>? {
    return mapsCollectionRef
      .whereGreaterThan(START_LATITUDE, southwest.latitude)
      .whereLessThan(START_LATITUDE, northeast.latitude)
      .get().await().documents.filter {
        val startLongitude = it.getDouble(START_LONGITUDE)!!
        ((southwest.longitude > 0 && northeast.longitude < 0) &&
          (southwest.longitude < startLongitude || startLongitude < northeast.longitude))
          || (southwest.longitude < startLongitude && startLongitude < northeast.longitude)
      }.map {
        NearMap(
          it.getString(MAP_ID)!!,
          it.getString(MAP_TITLE)!!,
          LatLng(it.getDouble(START_LATITUDE)!!, it.getDouble(START_LONGITUDE)!!),
          it.get(DISTANCE) as Double
        )
      }
  }

  fun incrementExecute(mapTitle: String) {
    mapsCollectionRef.document(mapTitle).update(PLAYS, FieldValue.increment(1))
  }

  fun uploadMap(infoData: InfoData, rankingData: RankingData, activityData: ActivityData, timestamp: String, gpxUri: Uri, imgPath: Uri) {
    //Maps/mapId에 새로운 맵 정보 생성
    db.collection(MAPS).document(infoData.mapId!!).set(infoData)
    //racerGPX
    FBStorageRepository().uploadFile(imgPath, infoData.mapImagePath!!)
    FBStorageRepository().uploadFile(gpxUri, infoData.routeGPXPath!!)
    FBStorageRepository().uploadFile(gpxUri, rankingData.racerGPX!!)
    db.collection(MAPS).document(infoData.mapId!!).collection(RANKING)
      .document(UserInfo.autoLoginKey + timestamp).set(rankingData)
    // 히스토리 업로드
    FBUsersRepository().createUserHistory(activityData)
  }

  suspend fun listPlayed(): List<String> {
    return db.collection(USERS).document(UserInfo.autoLoginKey).collection(ACTIVITIES)
      .get()
      .await()
      .documents.map {
        it.getString(MAP_ID)!!
      }
  }

  suspend fun listLikedMap(): List<String> {
    return db.collection(BaseFB.USERS).document(UserInfo.autoLoginKey).collection(BaseFB.LIKED_MAP)
      .get().await().documents.map {
        it.getString(BaseFB.MAP_ID)!!
      }
  }

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