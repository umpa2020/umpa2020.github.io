package com.umpa2020.tracer.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.umpa2020.tracer.App

fun Int.makingIcon(): BitmapDescriptor {
  // 기본 마커 활용해서
  val circleDrawable = App.instance.context().getDrawable(this)
  var canvas = Canvas()
  var bitmap = Bitmap.createBitmap(
    circleDrawable!!.intrinsicWidth,
    circleDrawable.intrinsicHeight,
    Bitmap.Config.ARGB_8888
  )
  canvas.setBitmap(bitmap)
  circleDrawable.setBounds(
    0,
    0,
    circleDrawable.intrinsicWidth,
    circleDrawable.intrinsicHeight
  )
  circleDrawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun Double.prettyDistance(): String {
  return if (this < 1000) {
    String.format("%.0f", this) + " m"
  } else {
    String.format("%.2f", this / 1000) + " km"
  }
}

fun Double.prettySpeed(): String {
  return String.format("%.1f", this) + "km/h"
}