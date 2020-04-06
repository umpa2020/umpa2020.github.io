package com.umpa2020.tracer.network

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.util.ProgressBar

class FBMapImage {
  val db = FirebaseFirestore.getInstance()

  fun getMapImage(imageView: ImageView, mapTitle: String) {
    val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
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