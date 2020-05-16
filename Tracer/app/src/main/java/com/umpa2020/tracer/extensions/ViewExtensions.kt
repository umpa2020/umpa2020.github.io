package com.umpa2020.tracer.extensions

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.GlideApp
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyGlideApp

fun ImageView.image(uri: Uri?) {
  uri?.let{
    GlideApp.with(App.instance.context())
      .load(it)
      .override(1024, 980)
      .error(R.drawable.ic_racer1)
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