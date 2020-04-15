package com.umpa2020.tracer.network

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Profile fragment 에서 프로필을 설정하는 네트워크 클래스
 *
 */

class FBProfile {
  val MYROUTE = 60 // 마이 루트가 존재
  val MYROUTEFAIL = 70 // 마이 루트가 없을 경우
  val CHANGEPROFILE = 110 // 프로필 사진 체인지
  val db = FirebaseFirestore.getInstance()
  var profileImagePath = "init"

  /**
   * 프로필 프래그먼트, 다른 사람 프로필 액티비티의
   * 내용을 채워주는 함수,
   * 받은 닉네임으로 uid를 db에서 찾아서
   * 해당 사용자가 뛴 거리, 뛴
   */

  fun setProfile(view: View, nickname: String, profileListener: ProfileListener) {
    var uid = "init"
    db.collection("userinfo").whereEqualTo("nickname", nickname)
      .get()
      .addOnSuccessListener {
        for (document in it) {
          uid = document.get("UID") as String
          break
        }
        // 총 거리, 총 시간을 구하기 위해서 db에 접근하여 일단 먼저
        // 이용자가 뛴 다른 사람의 맵을 구함
        db.collection("userinfo").document(uid).collection("user ran these maps")
          .get()
          .addOnSuccessListener { result ->
            var sumDistance = 0.0
            var sumTime = 0.0
            for (document in result) {
              sumDistance += document.get("distance") as Double
              sumTime += document.get("time") as Long
            }
            // 구하고 나서 이용자가 만든 맵의 거리와 시간을 더함
            db.collection("mapInfo").whereEqualTo("makersNickname", nickname)
              .get()
              .addOnSuccessListener { result2 ->
                for (document2 in result2) {
                  sumDistance += document2.get("distance") as Double
                  sumTime += document2.get("time") as Long
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
    db.collection("userinfo").whereEqualTo("nickname", nickname)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          profileImagePath = document.get("profileImagePath") as String
          uid = document.get("UID") as String
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
  fun changeProfileImage(bitmapImg: Bitmap, mHandler: Handler) {
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()

    val dt = Date()
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜(영어).jpg 경로 만들기

    val mStorage = FirebaseStorage.getInstance()
    val mStorageReference = mStorage.reference

    val profileRef = mStorageReference.child("Profile").child(UserInfo.autoLoginKey).child("${dt.time}.jpg")
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

          mFirestoreDB.collection("userinfo").document(UserInfo.autoLoginKey)
            .update("profileImagePath", "Profile/${UserInfo.autoLoginKey}/${dt.time}.jpg")
            .addOnSuccessListener {

              val msg: Message = mHandler.obtainMessage(CHANGEPROFILE)
              msg.obj = true
              mHandler.sendMessage(msg)
              progressbar.dismiss()
            }
        }
      }

  }

  fun getRoute(mHandler: Handler, nickname: String) {
    val infoDatas: ArrayList<InfoData> = arrayListOf()
    db.collection("mapInfo").whereEqualTo("makersNickname", nickname).whereEqualTo("privacy", "RACING")
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val data = document.toObject(InfoData::class.java)
          infoDatas.add(data)
          Logg.d("ssmm11 infodata = $data / " + document.id)
        }
        val msg: Message

        // 만든 맵이 없는 경우, 저장된 맵이 없다는 창을 띄우기
        // 만든 맵이 있는 경우, 내용 띄우기 msg.what 으로 구분
        if (result.isEmpty) {
          msg = mHandler.obtainMessage(MYROUTEFAIL)
        } else {
          msg = mHandler.obtainMessage(MYROUTE)
        }
        msg.obj = infoDatas
        mHandler.sendMessage(msg)
      }
      .addOnFailureListener { exception ->
      }
  }
}