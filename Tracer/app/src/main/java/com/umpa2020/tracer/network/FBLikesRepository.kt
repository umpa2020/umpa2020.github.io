package com.umpa2020.tracer.network

import com.google.firebase.firestore.FieldValue
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.dataClass.UserId
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

/**
 * 좋아요 관련 클래스
 * 사용법 - Likes().관련기능함수()
 */

class FBLikesRepository : BaseFB() {
  /**
   * uid가 mapid를 like 했는지 Boolean으로 반환
   */
  suspend fun isLiked(uid: String, mapId: String): Boolean {
    return !usersCollectionRef.document(uid).collection(LIKED_MAP)
      .whereEqualTo(MAP_ID, mapId)
      .get().await().isEmpty
  }

  /**
   * mapId의 likes를 반환
   */
  suspend fun getMapLikes(mapId: String): Int {
    return mapsCollectionRef.document(mapId).get().await().getLong(LIKES)!!.toInt()
  }

  /**
   * uid의 mapId 좋아요 상태를 바꾼다
   */
  suspend fun toggleLikes(uid: String, mapId: String) {
    if (isLiked(uid, mapId)) {
      usersCollectionRef.document(uid).collection(LIKED_MAP).whereEqualTo(MAP_ID, mapId)
        .get().await().documents.first().reference.delete()
      mapsCollectionRef.document(mapId).collection(LIKES).whereEqualTo(USER_ID, uid)
        .get().await().documents.first().reference.delete()
      mapsCollectionRef.document(mapId).update(LIKES, FieldValue.increment(-1))
    } else {
      usersCollectionRef.document(UserInfo.autoLoginKey).collection(LIKED_MAP)
        .add(LikedMapData(mapId))
      mapsCollectionRef.document(mapId).collection(LIKES).add(UserId(uid))
      mapsCollectionRef.document(mapId).update(LIKES, FieldValue.increment(1))
    }
  }
}