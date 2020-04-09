package com.umpa2020.tracer.extensions

import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import java.io.File

fun Location.toLatLng():LatLng{
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
  return LatLngBounds(LatLng(minlat,minlon), LatLng(maxlat,maxlon))
}

fun RouteGPX.classToGpx(folderPath: String): Uri {
  try {
    Logg.d( "make gpx file")
    var gpxBuilder = GPX.builder()
    var track = Track.builder()
    track.addSegment(TrackSegment.of(this.trkList))
    gpxBuilder.addTrack(track.build())
    this.wptList.forEach{ gpxBuilder.addWayPoint(it)}
    val gpx = gpxBuilder.build()
    val saveFolder = File(folderPath) // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val path = "route" + saveFolder.list()!!.size + ".gpx"
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
  return RouteGPX("test","Test",gpx.wayPoints,gpx.tracks[0].segments[0].points)
}