package com.umpa2020.tracer.network

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App

class FBMapImageRepository {
  val db = FirebaseFirestore.getInstance()

  fun getMapImage(imageView: ImageView, mapTitle: String) {
    val storage = FirebaseStorage.getInstance()
    val mapImageRef = storage.reference.child("mapImage").child(mapTitle)
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