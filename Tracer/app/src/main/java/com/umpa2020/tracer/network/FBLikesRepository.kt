package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.util.UserInfo

/**
 * 좋아요 관련 클래스
 * 사용법 - Likes().관련기능함수()
 */

class FBLikesRepository {
  val GETLIKE = 51

  val db = FirebaseFirestore.getInstance()

  /**
   * 유저 인포에 저장되어있는 해당 유저가 좋아요 한 맵을 검사하여
   * 리사이클러뷰에 메시지를 보낸다.
   *
   * -> 리사이클러뷰에서 해당 유저가 좋아요한 맵에 대해서 이미지 작업을 한다.
   */
  fun getLikes(listener: LikedMapListener) {
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps")
      .get()
      .addOnSuccessListener { result ->
        listener.likedList(result.map { LikedMapData(it.getString("mapTitle"), it.getString("uid")) })
      }
  }

  fun getLike(mapTitle: String, listener: LikedMapListener) {
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps")
      .whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener {


        db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
          .get()
          .addOnSuccessListener { result ->
            for (document in result) {
              val buffer = document.get("likes")
              // 사용자가 좋아한 맵에 현재 맵이 있으면 true 넘겨주고
              // 없으면 false 넘겨주기
              listener.liked(!it.isEmpty, Integer.parseInt(buffer.toString()))
            }
          }
      }
  }


  /**
   * 1. 유저가 좋아요 버튼을 눌렀을 때, 유저 인포에 좋아요한 맵을 저장하고
   * 2. 맵 인포에 유저 uid를 저장하고
   * 3. 맵 인포에 좋아요 숫자를 1 더한다
   */
  fun setLikes(maptitle: String, likes: Int) {
    val likeMapsData = LikedMapData(maptitle, UserInfo.autoLoginKey)
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps")
      .add(likeMapsData)
    db.collection("mapInfo").document(maptitle).collection("likes").add(likeMapsData)
    db.collection("mapInfo").document(maptitle).update("likes", likes + 1)
  }


  /**
   * 1. 유저가 이미 좋아요한 맵을 취소할 때, 유저 인포에서 좋아요한 맵을 찾아 삭제하고
   * 2. 맵 인포에서 유저 uid를 삭제하고
   * 3. 맵 인포에서 좋아요 수를 1 감소한 값으로 업데이트
   */
  fun setminusLikes(maptitle: String, likes: Int) {

    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user liked these maps")
      .whereEqualTo("mapTitle", maptitle)
      .get()
      .addOnSuccessListener { result ->
        val documentid = result.documents[0].id

        db.collection("userinfo").document(UserInfo.autoLoginKey)
          .collection("user liked these maps")
          .document(documentid).delete()
      }

    db.collection("mapInfo").document(maptitle).collection("likes")
      .whereEqualTo("mapTitle", maptitle)
      .get()
      .addOnSuccessListener { result ->
        val documentid = result.documents[0].id

        db.collection("mapInfo").document(maptitle).collection("likes")
          .document(documentid).delete()
      }
    db.collection("mapInfo").document(maptitle).update("likes", likes - 1)

  }
}