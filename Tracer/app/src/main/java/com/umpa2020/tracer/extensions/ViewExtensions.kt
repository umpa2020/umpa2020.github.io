package com.umpa2020.tracer.extensions

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.GlideApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun ImageView.image(uri: Uri?) {
  uri?.let {
    GlideApp.with(App.instance.context())
      .load(it)
      .override(1024, 980)
      .error(R.drawable.logosquare)
      .dontAnimate()
      .dontTransform()
      .into(this)
  }
}

fun View.visible() {
  visibility = View.VISIBLE
}

fun View.invisible() {
  visibility = View.INVISIBLE
}

fun View.gone() {
  visibility = View.GONE
}