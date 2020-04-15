package com.umpa2020.tracer.extensions

import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.GPX
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import java.io.File
import java.lang.Math.PI

fun Location.toLatLng(): LatLng {
  return LatLng(latitude.toDouble(), longitude.toDouble())
}

fun WayPoint.toLatLng(): LatLng {
  return LatLng(latitude.toDouble(), longitude.toDouble())
}

fun MutableList<LatLng>.bounds(): LatLngBounds {
  var minlat = first().latitude
  var maxlat = last().latitude
  var minlon = first().longitude
  var maxlon = last().longitude
  forEach {
    if (it.latitude < minlat) minlat = it.latitude
    else if (it.latitude > maxlat) maxlat = it.latitude
    if (it.longitude < minlon) minlon = it.longitude
    else if (it.longitude > maxlon) maxlon = it.longitude
  }
  return LatLngBounds(LatLng(minlat, minlon), LatLng(maxlat, maxlon))
}

fun RouteGPX.addDirectionSign(): RouteGPX {
  val latlngs = mutableListOf<LatLng>()
  this.trkList.forEach { latlngs.add(it.toLatLng()) }
  val simplifyLatLngs = PolyUtil.simplify(latlngs, 10.0)

  for (i in 1 until simplifyLatLngs.size - 1) {
    val a = simplifyLatLngs[i - 1]
    val b = simplifyLatLngs[i]
    val c = simplifyLatLngs[i + 1]
    /*val preGradient = (b.longitude - a.longitude) / (b.latitude - a.latitude)
    val postGradient = (c.longitude - b.longitude) / (c.latitude - b.latitude)
    val angle = (kotlin.math.atan(preGradient) - kotlin.math.atan(postGradient)) * 180 / PI*/
    val postGradient = (kotlin.math.atan((c.latitude - b.latitude)/ (c.longitude - b.longitude)))
    val preGradient = (kotlin.math.atan((b.latitude - a.latitude)/ (b.longitude - a.longitude)))
    val angle =
      when (checkQuadrant(a, b)) {
        1 -> (preGradient - postGradient) * 180 / PI
        2 -> (preGradient + postGradient) * 180 / PI
        3 -> (-preGradient - postGradient) * 180 / PI
        4 -> (-preGradient + postGradient) * 180 / PI
        else -> 0
      }
    Logg.d(((preGradient - postGradient) * 180 / PI).toString())
    Logg.d(((preGradient + postGradient) * 180 / PI).toString())
    Logg.d(((-preGradient - postGradient) * 180 / PI).toString())
    Logg.d(((-preGradient + postGradient) * 180 / PI).toString())
    Logg.d("pre = ${preGradient*180/PI} / post = ${postGradient*180/PI}")
    /*var direction = ""
    if (angle > 45) {
      direction =
        if (preGradient > postGradient) "left"
        else "right"
    }*/
  }


  return this
}

fun checkQuadrant(a: LatLng, b: LatLng): Int {
  var a=(if (b.latitude - a.latitude >= 0 && b.longitude - a.longitude >= 0) 1
  else if (b.latitude - a.latitude >= 0 && b.longitude - a.longitude <= 0) 2
  else if (b.latitude - a.latitude <= 0 && b.longitude - a.longitude <= 0) 3
  else if (b.latitude - a.latitude <= 0 && b.longitude - a.longitude >= 0) 4
  else 0)
  Logg.d("판별찡 $a")
  return a
}

//TODO : Kotlin scope 적용
fun RouteGPX.classToGpx(folderPath: String): Uri {
  val gpx = GPX.builder()
    .addTrack { it.addSegment(TrackSegment.of(trkList)).build() }
    .addWayPoint {
      this.wptList.map { wpt ->
        it.lat(wpt.latitude)
          .lon(wpt.longitude)
          .build()
      }
    }.build()

  try {
    Logg.d("make gpx file")
    /* val gpxBuilder = GPX.builder()
     val track = Track.builder().addSegment(TrackSegment.of(this.trkList))
     gpxBuilder.addTrack(track.build())
     this.wptList.forEach { gpxBuilder.addWayPoint(it) }
     val gpx = gpxBuilder.build()*/

    val saveFolder = File(folderPath) // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val path = "route_${System.currentTimeMillis()}.gpx"
    val myfile = File(saveFolder, path)         //로컬에 파일저장
    GPX.write(gpx, (myfile.path))
    Logg.d("start upload gpx")
    return Uri.fromFile(myfile)
  } catch (e: Exception) {
    Logg.d(e.toString());
  }
  return Uri.EMPTY
}

fun String.gpxToClass(): RouteGPX {
  val gpx = GPX.read(this)
  return RouteGPX("test", "Test", gpx.wayPoints, gpx.tracks[0].segments[0].points)
}