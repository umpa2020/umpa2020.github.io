package com.umpa2020.tracer.network

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.dataClass.PlayedMapData
import com.umpa2020.tracer.network.BaseFB.Companion.PLAYS
import com.umpa2020.tracer.network.BaseFB.Companion.LIKES
import com.umpa2020.tracer.network.BaseFB.Companion.MAPS
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await


/**
 * 랭킹 네트워크 클래스 - 랭킹에 관련한
 * 네트워크 접근 함수는 이 곳에 정의
 */
class FBRankingRepository : BaseFB() {
  lateinit var globalStartAfter: DocumentSnapshot

  /**
   * 현재 위치를 받아서 현재 위치와 필터에 적용한 위치 만큼 떨어져 있는 구간에서 실행순으로 정렬한 코드
   */
  suspend fun listRanking(
    cur_loc: LatLng,
    boundary: Int,
    mode: String,
    limit: Long
  ): MutableList<InfoData> {
    /**
     * 결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
     * Location 형태로 받아오고 싶다면 아래처럼
     * var getintentLocation = current
     */

    val infoDatas = db.collection(MAPS)
      .orderBy(mode, Query.Direction.DESCENDING)
      .limit(limit)
      .get().await().documents.map {
        globalStartAfter = it
        it.toObject(InfoData::class.java)!!.apply {
          distance = SphericalUtil.computeDistanceBetween(
            cur_loc,
            LatLng(startLatitude, startLongitude)
          )
        }
      }.filter {
        it.distance!! < boundary * 1000
      }.toMutableList()

    val playedMapIdList = FBMapRepository().listPlayed()
    val likedMapIdList = FBMapRepository().listLikedMap()
    infoDatas.filter { playedMapIdList.contains(it.mapId) }.forEach { it.played = true }
    infoDatas.filter { likedMapIdList.contains(it.mapId) }.forEach { it.liked = true }
    return infoDatas
  }

  suspend fun listFilterRange(
    cur_loc: LatLng,
    boundary: Int,
    mode: String,
    limit: Long
  ): MutableList<InfoData> {
    //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
    //Location 형태로 받아오고 싶다면 아래처럼
    //var getintentLocation = current

    val infoDatas = db.collection(MAPS)
      .orderBy(mode, Query.Direction.DESCENDING)
      .startAfter(globalStartAfter)
      .limit(limit)
      .get().await().documents.map {
        globalStartAfter = it
        it.toObject(InfoData::class.java)!!.apply {
          distance = SphericalUtil.computeDistanceBetween(
            cur_loc,
            LatLng(startLatitude!!, startLongitude!!)
          )
        }
      }.filter {
        it.distance!! < boundary * 1000
      }.toMutableList()

    val playedMapIdList = FBMapRepository().listPlayed()
    val likedMapIdList = FBMapRepository().listLikedMap()
    infoDatas.filter { playedMapIdList.contains(it.mapId) }.forEach { it.played = true }
    infoDatas.filter { likedMapIdList.contains(it.mapId) }.forEach { it.liked = true }
    return infoDatas
  }
}