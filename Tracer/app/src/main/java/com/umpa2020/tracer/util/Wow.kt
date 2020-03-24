package com.umpa2020.tracer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.text.SimpleDateFormat
import java.util.*

class Wow {
  companion object {
    // array에서 max, min 받아와서 처리
    fun minDoubleLat(list: MutableList<MutableList<LatLng>>): Double {
      var min = list[0][0].latitude
      for (i in list.indices) {
        for (j in list[i].indices) {
          if (list[i][j].latitude < min) {
            min = list[i][j].latitude
          }
        }
      }
      return min
    }

    fun minDoubleLng(list: MutableList<MutableList<LatLng>>): Double {
      var min = list[0][0].longitude
      for (i in list.indices) {
        for (j in list[i].indices) {
          if (list[i][j].longitude < min) {
            min = list[i][j].longitude
          }
        }
      }
      return min
    }

    fun maxDoubleLat(list: MutableList<MutableList<LatLng>>): Double {
      var max = list[0][0].latitude
      for (i in list.indices) {
        for (j in list[i].indices) {
          if (list[i][j].latitude > max) {
            max = list[i][j].latitude
          }
        }
      }
      return max
    }

    fun maxDoubleLng(list: MutableList<MutableList<LatLng>>): Double {
      var max = list[0][0].longitude
      for (i in list.indices) {
        for (j in list[i].indices) {
          if (list[i][j].longitude > max) {
            max = list[i][j].longitude
          }
        }
      }
      return max
    }

    // gps 파일에서 처리
    fun getDistance(locations: MutableList<LatLng>): Double {  //점들의 집합에서 거리구하기
      var distance = 0.0
      var i = 0
      while (i < locations.size - 1) {
        distance += SphericalUtil.computeDistanceBetween(locations[i], locations[i + 1])
        i++
      }
      return distance
    }

    // date formmatter
    fun milisecToString(milisec: Long): String {
      val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
      formatter.timeZone = TimeZone.getTimeZone("UTC")

      return formatter.format(Date(milisec))
    }

    /**
     *
     *
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