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

    var preGradient =
      (kotlin.math.atan2((b.latitude - a.latitude), (b.longitude - a.longitude))) * 180 / PI
    var postGradient =
      (kotlin.math.atan2((c.latitude - b.latitude), (c.longitude - b.longitude))) * 180 / PI

    Logg.d("보정 전 pre = $preGradient / post = $postGradient")

    if (preGradient - postGradient < -180.0 || preGradient - postGradient > 180.0) {
      if (preGradient < 0) {
        preGradient += 360.0
      }
      if (postGradient < 0) {
        postGradient += 360.0
      }
    }

    val angle = preGradient - postGradient
    Logg.d("angle = $angle")
  }
  return this
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