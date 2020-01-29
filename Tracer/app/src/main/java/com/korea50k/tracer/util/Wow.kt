package com.korea50k.tracer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.text.SimpleDateFormat
import java.util.*

class Wow(){
    companion object {
        // array에서 max, min 받아와서 처리
        fun minDoubleLat(list:  MutableList<LatLng>): Double {
            var min = list[0].latitude
            for (i in list.indices) {
                if (list[i].latitude < min) {
                    min = list[i].latitude
                }
            }
            return min
        }

        fun minDoubleLng(list:  MutableList<LatLng>): Double {
            var min = list[0].longitude
            for (i in list.indices) {
                if (list[i].longitude < min) {
                    min = list[i].longitude
                }
            }
            return min
        }

        fun maxDoubleLat(list:  MutableList<LatLng>): Double {
            var max = list[0].latitude
            for (i in list.indices) {
                if (list[i].latitude > max) {
                    max = list[i].latitude
                }
            }
            return max
        }

        fun maxDoubleLng(list:  MutableList<LatLng>): Double {
            var max = list[0].longitude
            for (i in list.indices) {
                if (list[i].longitude > max) {
                    max = list[i].longitude
                }
            }
            return max
        }

        // gps 파일에서 처리
        fun getDistance(locations: Vector<LatLng>): Double {  //점들의 집합에서 거리구하기
            var distance = 0.0
            var i = 0
            while (i < locations.size - 1) {
                distance += SphericalUtil.computeDistanceBetween(locations[i], locations[i + 1])
                i++
            }
            return distance
        }

        // date formmatter
        fun milisecToString(milisec:Long):String{
            val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

            return formatter.format(Date(milisec))
        }

        /**
         *
         *
         */
        fun makingIcon(drawable: Int,context: Context): BitmapDescriptor {
            // 기본 마커 활용해서
            val circleDrawable =  context.getDrawable(drawable)
            var canvas = Canvas()
            var bitmap = Bitmap.createBitmap(
                circleDrawable!!.intrinsicWidth,
                circleDrawable!!.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            canvas.setBitmap(bitmap);
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