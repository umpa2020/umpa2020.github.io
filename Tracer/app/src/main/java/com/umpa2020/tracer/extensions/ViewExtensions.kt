package com.umpa2020.tracer.extensions

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.GlideApp

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