package com.umpa2020.tracer.extensions

import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_ANGLE
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_LEFT_POINT
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_RIGHT_POINT
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.GPX
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

fun Location.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
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
  val simplifyLatLngs = PolyUtil.simplify(latlngs, 20.0)

  for (i in 1 until simplifyLatLngs.size - 1) {
    val a = simplifyLatLngs[i - 1]
    val b = simplifyLatLngs[i]
    val c = simplifyLatLngs[i + 1]
    //이전과 현재 좌표직선의 기울기
    var preGradient = (atan2((b.latitude - a.latitude), (b.longitude - a.longitude))) * 180 / PI
    //현재와 다음 좌표직선의 기울기
    var postGradient =
      (atan2((c.latitude - b.latitude), (c.longitude - b.longitude))) * 180 / PI
    //만약 값이 범위를 초과하면 보정
    if (postGradient - preGradient < 180.0 || postGradient - preGradient > -180.0) {
      if (preGradient < 0) {
        preGradient += 360.0
      }
      if (postGradient < 0) {
        postGradient += 360.0
      }
    }
    //직선의 회전각
    val angle = postGradient - preGradient
    Logg.d("angle = $angle")
    if (abs(angle) >= TURNING_ANGLE) {
      if (angle > 0) {
        this.wptList.add(
          WayPoint.builder()
            .lat(b.latitude)
            .lon(b.longitude)
            .name("Turning Point")
            .desc("Turn Left")
            .type(TURNING_LEFT_POINT)
            .build()
        )
      } else {
        this.wptList.add(
          WayPoint.builder()
            .lat(b.latitude)
            .lon(b.longitude)
            .name("Turning Point")
            .desc("Turn Right")
            .type(TURNING_RIGHT_POINT)
            .build()
        )
      }

    }
  }
  return this
}

//TODO : Kotlin scope 적용
fun RouteGPX.classToGpx(folderPath: String): Uri {
  val gpxBuilder = GPX.builder()
    .addTrack { it.addSegment(TrackSegment.of(trkList)).build() }
  wptList.forEach { gpxBuilder.addWayPoint(it) }
  val gpx = gpxBuilder.build()
  try {
    Logg.d("make gpx file")
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