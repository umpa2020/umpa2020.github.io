package com.umpa2020.tracer.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.umpa2020.tracer.App

fun ImageView.image(uri: Uri?) {
  if (uri == null) {

  } else {
    Glide.with(App.instance.context())
      .load(uri)
      .override(1024, 980)
      .into(this)
  }
}