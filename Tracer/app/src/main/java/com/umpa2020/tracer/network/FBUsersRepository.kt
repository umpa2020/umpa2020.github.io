package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.umpa2020.tracer.dataClass.*
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

/**
 * 유저 히스토리 관련 클래스
 * 사용법 - FBUserHistoryRepository().관련기능함수()
 *
 * 1. 유저가 뛴 맵 history 저장
 * 2. 유저가 만든 맵 history 저장
 * 3. 유저가 challenge 한 맵 history 저장
 */

class FBUsersRepository : BaseFB() {
  var globalStartAfter: DocumentSnapshot? = null

  suspend fun listUserAchievement(userId: String): ArrayList<Int> {
    val list = arrayListOf<Int>()

    val ref = usersCollectionRef.whereEqualTo(USER_ID, userId).get().await()
      .documents.first().reference
    for (i in 1..3) {
      list.add(
        ref.collection(TROPHIES).whereEqualTo(RANKING, i).get().await()
          .documents.size
      )
    }
    return list
  }

  fun createUserAchievement(achieveData: TrophyData) {
    usersCollectionRef.document(UserInfo.autoLoginKey).collection(TROPHIES).add(achieveData)
  }

  fun updateUserAchievement(rankingDatas: MutableList<RankingData>, mapId: String) {
    var ranking = 0L
    run loop@{
      rankingDatas.forEachIndexed { i, rankingData ->
        if (i > 3)
          return@loop
        if (rankingData.challengerId == UserInfo.autoLoginKey) {
          ranking = i + 1L
        }
      }
    }


    if (ranking != 0L) {
      usersCollectionRef.document(UserInfo.autoLoginKey).collection(TROPHIES)
        .whereEqualTo(MAP_ID, mapId)
        .get()
        .addOnSuccessListener {
          if (it.isEmpty) {
            usersCollectionRef.document(UserInfo.autoLoginKey).collection(TROPHIES)
              .add(TrophyData(mapId, ranking))
          } else {
            val before = it.documents.first().getLong(RANKING)!!
            if (before != ranking) {
              it.documents.first().reference.update(RANKING, ranking)
            }
          }
        }
    }
  }

  fun createUserInfo(data: HashMap<String, String?>) {
    db.collection(USERS).document(data[USER_ID]!!).set(data)
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully written!") }
      .addOnFailureListener { e -> Logg.w("Error writing document$e") }

    FBAchievementRepository().createAchievement(data[USER_ID]!!)
  }

  fun createUserHistory(activityData: ActivityData) {
    db.collection(USERS).whereEqualTo(USER_ID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(ACTIVITIES).add(activityData)
      }
  }

  suspend fun listUserMakingActivity(limit: Long): List<ActivityData>? {
    return (if (globalStartAfter == null) db.collection(USERS).document(UserInfo.autoLoginKey).collection(ACTIVITIES)
    else usersCollectionRef.document(UserInfo.autoLoginKey).collection(ACTIVITIES).startAfter(globalStartAfter!!))
      .limit(limit).get().await().apply {
        if (documents.isEmpty())
          return null
        globalStartAfter = last()
      }.toObjects(ActivityData::class.java)
  }

  suspend fun listUserRoute(uid: String, limit: Long): List<InfoData>? {
    val infoDatas =
      if (globalStartAfter == null) {
        mapsCollectionRef.whereEqualTo(BaseFB.MAKER_ID, uid)
      } else {
        mapsCollectionRef.whereEqualTo(BaseFB.MAKER_ID, uid).startAfter(globalStartAfter!!)
      }.limit(limit).get().await().apply {
        if (documents.size == 0)
          return null
        globalStartAfter = documents.last()
      }.toObjects(InfoData::class.java)

    val playedMapIdList = FBMapRepository().listPlayed()
    val likedMapIdList = FBMapRepository().listLikedMap()
    infoDatas.filter { playedMapIdList.contains(it.mapId) }.forEach { it.played = true }
    infoDatas.filter { likedMapIdList.contains(it.mapId) }.forEach { it.liked = true }
    return infoDatas
  }

  /**
   * uid가 mapid를 like 했는지 Boolean으로 반환
   */
  suspend fun isPlayed(uid: String, mapId: String): Boolean {
    return !usersCollectionRef.document(uid).collection(ACTIVITIES)
      .whereEqualTo(MAP_ID, mapId)
      .get().await().isEmpty
  }
}