package com.umpa2020.tracer.network

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.umpa2020.tracer.dataClass.ProfileData
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Profile fragment 에서 프로필을 설정하는 네트워크 클래스
 */

class FBProfileRepository : BaseFB() {
  lateinit var globalStartAfter: DocumentSnapshot

  suspend fun getUserNickname(uid: String): String {
    return db.collection(USERS).whereEqualTo(USER_ID, uid)
      .get()
      .await()
      .documents.first().getString(NICKNAME) ?: ""
  }

  /**
   * 프로필 프래그먼트, 다른 사람 프로필 액티비티의
   * 내용을 채워주는 함수,
   * 받은 닉네임으로 uid를 db에서 찾아서
   * 해당 사용자가 뛴 거리, 뛴
   */
  suspend fun getProfile(uid: String): ProfileData {
    return db.collection(USERS).document(uid).get().await().let {
      var sumDistance = 0.0
      var sumTime = 0L
      it.reference.collection(ACTIVITIES).get().await().documents.forEach {
        sumDistance += it.get(DISTANCE) as Double
        sumTime += it.get(PLAY_TIME) as Long
      }
      ProfileData(sumDistance, sumTime, FBStorageRepository().downloadFile(it.getString(PROFILE_IMAGE_PATH)!!)!!)
    }
  }

  suspend fun getProfileImage(uid: String): Uri? {
    return FBStorageRepository().downloadFile(
      db.collection(USERS).whereEqualTo(USER_ID, uid)
        .get().await().documents.first().getString(PROFILE_IMAGE_PATH)!!
    )
  }

  /**
   * 사진 변경 시, 해당 사진을 storage에 업로드하고
   * 그 경로를 db에 update하는 함수
   */
  suspend fun updateProfileImage(selectedImageUri: Uri) {
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜(영어).jpg 경로 만들기
    val dt = Date()
    FBStorageRepository().uploadFile(selectedImageUri, PROFILE + "/" + UserInfo.autoLoginKey + "/" + dt.time.toString())
    db.collection(USERS).document(UserInfo.autoLoginKey)
      .update(PROFILE_IMAGE_PATH, "$PROFILE/${UserInfo.autoLoginKey}/${dt.time}").await()
  }

  /**
   * 회원 가입 시 프로필 이미지 storage에 등록
   */
  fun uploadProfileImage(imageUri: Uri, timestamp: String) {
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜 경로 만들기
    FBStorageRepository().uploadFile(imageUri, PROFILE + "/" + UserInfo.autoLoginKey + "/" + timestamp)
  }

}
