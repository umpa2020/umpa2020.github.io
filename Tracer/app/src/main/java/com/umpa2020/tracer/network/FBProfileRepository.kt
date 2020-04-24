package com.umpa2020.tracer.network

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.network.BaseFB.Companion.DISTANCE
import com.umpa2020.tracer.network.BaseFB.Companion.MAKERS_NICKNAME
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_INFO
import com.umpa2020.tracer.network.BaseFB.Companion.NICKNAME
import com.umpa2020.tracer.network.BaseFB.Companion.PRIVACY
import com.umpa2020.tracer.network.BaseFB.Companion.PROFILE
import com.umpa2020.tracer.network.BaseFB.Companion.PROFILE_IMAGE_PATH
import com.umpa2020.tracer.network.BaseFB.Companion.RACING
import com.umpa2020.tracer.network.BaseFB.Companion.TIME
import com.umpa2020.tracer.network.BaseFB.Companion.UID
import com.umpa2020.tracer.network.BaseFB.Companion.USER_INFO
import com.umpa2020.tracer.network.BaseFB.Companion.USER_RAN_THESE_MAPS
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Profile fragment 에서 프로필을 설정하는 네트워크 클래스
 *
 */

class FBProfileRepository {
  val db = FirebaseFirestore.getInstance()
  var profileImagePath = "init"
  lateinit var globalStartAfter: DocumentSnapshot

  /**
   * 프로필 프래그먼트, 다른 사람 프로필 액티비티의
   * 내용을 채워주는 함수,
   * 받은 닉네임으로 uid를 db에서 찾아서
   * 해당 사용자가 뛴 거리, 뛴
   */

  fun getProfile(view: View, nickname: String, profileListener: ProfileListener) {
    var uid = "init"
    db.collection(USER_INFO).whereEqualTo(NICKNAME, nickname)
      .get()
      .addOnSuccessListener {
        for (document in it) {
          uid = document.get(UID) as String
          break
        }
        // 총 거리, 총 시간을 구하기 위해서 db에 접근하여 일단 먼저
        // 이용자가 뛴 다른 사람의 맵을 구함
        db.collection(USER_INFO).document(uid).collection(USER_RAN_THESE_MAPS)
          .get()
          .addOnSuccessListener { result ->
            var sumDistance = 0.0
            var sumTime = 0.0
            for (document in result) {
              sumDistance += document.get(DISTANCE) as Double
              sumTime += document.get(TIME) as Long
            }
            // 구하고 나서 이용자가 만든 맵의 거리와 시간을 더함
            db.collection(MAP_INFO).whereEqualTo(MAKERS_NICKNAME, nickname)
              .get()
              .addOnSuccessListener { result2 ->
                for (document2 in result2) {
                  sumDistance += document2.get(DISTANCE) as Double
                  sumTime += document2.get(TIME) as Long
                }
                profileListener.getProfile(sumDistance, sumTime)
              }
          }
        val imageView = view.findViewById<ImageView>(R.id.profileImageView)
        getProfileImage(imageView, nickname)
      }
  }

  fun getProfileImage(imageView: ImageView, nickname: String) {
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()
    var uid = "init"
    // storage 에 올린 경로를 db에 저장해두었으니 다시 역 추적 하여 프로필 이미지 반영
    db.collection(USER_INFO).whereEqualTo(NICKNAME, nickname)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          profileImagePath = document.get(PROFILE_IMAGE_PATH) as String
          uid = document.get(UID) as String
          break
        }
        // glide imageview 소스
        // 프사 설정하는 코드 db -> imageView glide
        val storage = FirebaseStorage.getInstance()
        val profileRef = storage.reference.child(profileImagePath)

        profileRef.downloadUrl.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            // Glide 이용하여 이미지뷰에 로딩
            Glide.with(App.instance.currentActivity() as Activity)
              .load(task.result)
              .override(1024, 980)
              .into(imageView)
            progressbar.dismiss()
          } else {
            progressbar.dismiss()
          }
        }
      }
      .addOnFailureListener { exception ->
      }
  }

  /**
   * 사진 변경 시, 해당 사진을 storage에 업로드하고
   * 그 경로를 db에 update하는 함수
   */
  fun updateProfileImage(bitmapImg: Bitmap, profileListener: ProfileListener) {
    val dt = Date()
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜(영어).jpg 경로 만들기

    val mStorage = FirebaseStorage.getInstance()
    val mStorageReference = mStorage.reference

    val profileRef =
      mStorageReference.child(PROFILE).child(UserInfo.autoLoginKey).child("${dt.time}.jpg")
    // 이미지
    val baos = ByteArrayOutputStream()
    bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageData = baos.toByteArray()

    // storage에 이미지 올리는 거
    val uploadTask = profileRef.putBytes(imageData)
    uploadTask
      .addOnCompleteListener {
        if (it.isSuccessful) {
          val mFirestoreDB = FirebaseFirestore.getInstance()

          mFirestoreDB.collection(USER_INFO).document(UserInfo.autoLoginKey)
            .update(PROFILE_IMAGE_PATH, "$PROFILE/${UserInfo.autoLoginKey}/${dt.time}.jpg")
            .addOnSuccessListener {
              profileListener.changeProfile()
            }
        }
      }
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
            }.map { it.myLiked = true }
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

    Logg.d("ssmm11 startAfter = $globalStartAfter" )
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
            }.map { it.myLiked = true }
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