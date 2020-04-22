package com.umpa2020.tracer.network

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App

/**
 * 맵 이미지만 가져오는 Repository
 */

class FBMapImageRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 바로 imageView 를 가져와서 등록한다.
   */

  fun getMapImage(imageView: ImageView, mapTitle: String) {
    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener {
        val mapImagePath = it.documents.last().get("mapImage") as String

        val storage = FirebaseStorage.getInstance()
        val mapImageRef = storage.reference.child(mapImagePath)
        mapImageRef.downloadUrl.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            // Glide 이용하여 이미지뷰에 로딩
            Glide.with(App.instance.context())
              .load(task.result)
              .override(1024, 980)
              .into(imageView)
          }
        }
      }


  }
}