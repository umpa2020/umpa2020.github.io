package com.umpa2020.tracer.network

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.ProgressBar

/**
 * 맵 이미지만 가져오는 Repository
 */

class FBImageRepository: BaseFB() {
  val progressbar = MyProgressBar()

  fun getMapImagePath(imageView: ImageView, mapTitle: String) {
    db.collection(MAP_INFO).whereEqualTo(MAP_TITLE, mapTitle)
      .get()
      .addOnSuccessListener {
        val mapImagePath = it.documents.last().get(MAP_IMAGE) as String
        getImage(imageView, mapImagePath)
      }
  }

  /**
   * 바로 imageView 를 가져와서 등록한다.
   */

  fun getImage(imageView: ImageView, path: String) {
    progressbar.show()
    val imageRef = storage.reference.child(path)
    imageRef.downloadUrl.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Glide 이용하여 이미지뷰에 로딩
        Glide.with(App.instance.context())
          .load(task.result)
          .override(1024, 980)
          .into(imageView)
        progressbar.dismiss()
      }
      else {
        progressbar.dismiss()
      }
    }
  }
}
