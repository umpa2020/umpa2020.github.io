package com.umpa2020.tracer.network

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.umpa2020.tracer.App
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_TITLE

/**
 * 대회 이미지만 가져오는 Repository
 */

class FBChallengeImageRepository : BaseFB() {

  /**
   * 바로 imageView 를 가져와서 등록한다.
   */

  fun getChallengeImage(imageView: ImageView, imagePath: String) {
    storage.reference.child(imagePath).downloadUrl
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          imageView.image(task.result!!)
        }
      }

  }
}
