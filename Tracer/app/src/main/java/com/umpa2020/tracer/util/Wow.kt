package com.umpa2020.tracer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class Wow {
  companion object {

    /**
     *drawble로 아이콘 제작
     */
    fun makingIcon(drawable: Int, context: Context): BitmapDescriptor {
      // 기본 마커 활용해서
      val circleDrawable = context.getDrawable(drawable)
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
  }
}