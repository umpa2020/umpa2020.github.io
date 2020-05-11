package com.umpa2020.tracer.network

import android.net.Uri
import android.widget.ImageView
import com.google.firebase.firestore.DocumentSnapshot
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.dataClass.ProfileData
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Profile fragment 에서 프로필을 설정하는 네트워크 클래스
 *
 */

class CoroutineTestRepository : BaseFB() {
  var profileImagePath = "init"
  lateinit var globalStartAfter: DocumentSnapshot

  /**
   * 프로필 프래그먼트, 다른 사람 프로필 액티비티의
   * 내용을 채워주는 함수,
   * 받은 닉네임으로 uid를 db에서 찾아서
   * 해당 사용자가 뛴 거리, 뛴
   */
  suspend fun getProfileImage(uid: String) :Uri?{
    return db.collection(USERS).whereEqualTo(USER_ID, uid)
        .get().await().let {
          FBStorageRepository().downloadFile(it.first().getString(PROFILE_IMAGE_PATH)!!)
        }
  }
  suspend fun getProfile(uid: String): ProfileData {
    return db.collection(USERS).document(uid).get().await().let {
      var sumDistance = 0.0
      var sumTime = 0L
      it.reference.collection(ACTIVITIES).get().await().documents.forEach {
        sumDistance += it.get(DISTANCE) as Double
        sumTime += it.get(TIME) as Long
      }
      ProfileData(sumDistance, sumTime,FBStorageRepository().downloadFile(it.getString(PROFILE_IMAGE_PATH)!!)!! )
    }
  }

  fun getProfileImage(imageView: ImageView, nickname: String) {
    // storage 에 올린 경로를 db에 저장해두었으니 다시 역 추적 하여 프로필 이미지 반영
    db.collection(USERS).whereEqualTo(NICKNAME, nickname)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          profileImagePath = document.get(PROFILE_IMAGE_PATH) as String
          break
        }
        FBImageRepository().getImage(imageView, profileImagePath)
      }
  }

  /**
   * 사진 변경 시, 해당 사진을 storage에 업로드하고
   * 그 경로를 db에 update하는 함수
   */
  fun updateProfileImage(selectedImageUri: Uri?, profileListener: ProfileListener) {
    val dt = Date()
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜(영어).jpg 경로 만들기

    db.collection(USERS).document(UserInfo.autoLoginKey)
      .update(PROFILE_IMAGE_PATH, "$PROFILE/${UserInfo.autoLoginKey}/${dt.time}")
      .addOnSuccessListener {
        profileListener.changeProfile()
      }
    FBStorageRepository().uploadFile(selectedImageUri!!, BaseFB.PROFILE + "/" + UserInfo.autoLoginKey + "/" + dt.time.toString())
  }

  /**
   * 회원 가입 시 프로필 이미지 storage에 등록
   */
  fun uploadProfileImage(imageUri: Uri, timestamp: String) {
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜 경로 만들기
    FBStorageRepository().uploadFile(imageUri, PROFILE + "/" + UserInfo.autoLoginKey + "/" + timestamp)
  }

  /**
   * 처음만 프로필에서 해당 유저가 만든 맵 띄우기
   */
  fun getRouteFirst(profileRouteListener: ProfileRouteListener, nickname: String, limit: Long) {
    val infoDatas = arrayListOf<InfoData>()

    db.collection("mapInfo").whereEqualTo("makersNickname", nickname)
      .whereEqualTo("privacy", "RACING")
      .limit(limit)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val data = document.toObject(InfoData::class.java)
          infoDatas.add(data)
          globalStartAfter = document
        }

        // 좋아요 필터를 눌렀을 때, 유저가 좋아요 누른 맵들을 가져오는 리스너
        val likedMapListener = object : LikedMapListener {
          override fun likedList(likedMaps: List<LikedMapData>) {
            infoDatas.filter { infoData ->
              likedMaps.map { it.mapTitle }
                .contains(infoData.mapTitle)
            }.map { it.isLiked = true }
            profileRouteListener.listProfileRoute(infoDatas)
          }

          override fun liked(liked: Boolean, likes: Int) {
          }
        }
        // 받아온 자신이 만든 맵 리스너로 보내기
        FBLikesRepository().listLikedMap(likedMapListener)
      }
  }

  /**
   * 프로필에서 해당 유저가 만든 맵 띄우기
   */
  fun getRoute(profileRouteListener: ProfileRouteListener, nickname: String, limit: Long) {
    val infoDatas = arrayListOf<InfoData>()

    Logg.d("ssmm11 startAfter = $globalStartAfter")
    db.collection("mapInfo").whereEqualTo("makersNickname", nickname)
      .whereEqualTo("privacy", "RACING")
      .startAfter(globalStartAfter)
      .limit(limit)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val data = document.toObject(InfoData::class.java)
          infoDatas.add(data)
          globalStartAfter = document
        }
        Logg.d("ssmm11 result size = ${result.size()}")

        // 좋아요 필터를 눌렀을 때, 유저가 좋아요 누른 맵들을 가져오는 리스너
        val likedMapListener = object : LikedMapListener {
          override fun likedList(likedMaps: List<LikedMapData>) {
            infoDatas.filter { infoData ->
              likedMaps.map { it.mapTitle }
                .contains(infoData.mapTitle)
            }.map { it.isLiked = true }
            profileRouteListener.listProfileRoute(infoDatas)
          }

          override fun liked(liked: Boolean, likes: Int) {
          }
        }
        // 받아온 자신이 만든 맵 리스너로 보내기
        FBLikesRepository().listLikedMap(likedMapListener)
      }
  }
}