package com.umpa2020.tracer.extensions

import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_ANGLE
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.util.Logg
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

fun Location.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
}

fun WayPoint.toLatLng(): LatLng {
  return LatLng(lat, lon)
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

fun RouteGPX.addCheckPoint(): RouteGPX {
  if (wptList.isNullOrEmpty()) wptList = mutableListOf()
  this.wptList.add(trkList.first().apply {
    name = "Start"
    desc = "Start Point"
    type = START_POINT
  })

  val latlngs = mutableListOf<LatLng>()
  var distance = 0.0
  trkList.forEachIndexed { i, it ->
    if (i > 0) {
      if (i == trkList.size - 1) {
        wptList.add(it.apply {
          name = "Finish"
          desc = "Finish Point"
          type = FINISH_POINT
        })
      } else {
        distance += SphericalUtil.computeDistanceBetween(
          trkList[i - 1].toLatLng(),
          it.toLatLng()
        )
        if (distance > 500) {
          distance = 0.0
          wptList.add(it.apply {
            name = "Distance point"
            desc = "100m"
            type = DISTANCE_POINT
          })
        }
      }
    }
  }
  return this
}

fun RouteGPX.addDirectionSign(): RouteGPX {
  val latlngs = mutableListOf<LatLng>()
  this.trkList.forEach { latlngs.add(it.toLatLng()) }
  val simplifyLatLngs = PolyUtil.simplify(latlngs, 20.0)
  if (wptList.isNullOrEmpty()) wptList = mutableListOf()
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
    if (abs(angle) >= TURNING_ANGLE) {
      if (angle > 0) {
        this.wptList.add(
          WayPoint(b.latitude, b.longitude, 0.0, 0.0, "Turning Point", "Turn Left", 0L, TURNING_LEFT_POINT)
        )
      } else {
        this.wptList.add(
          WayPoint(b.latitude, b.longitude, 0.0, 0.0, "Turning Point", "Turn Right", 0L, TURNING_RIGHT_POINT)
        )
      }

    }
  }
  return this
}

//TODO : Kotlin scope 적용
fun RouteGPX.classToGpx(folderPath: String): Uri {
  /*try {
    val document =
      DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    val trkpt = document.createElement("trkpt")
    trkpt.setAttribute("lat", "-33.626932")
    trkpt.setAttribute("lon", "-33.626932")
    val ele = document.createElement("ele")
    ele.appendChild(document.createTextNode("-6"))
    trkpt.appendChild(ele)
    document.appendChild(trkpt)
    val transformer = TransformerFactory.newInstance().newTransformer()
    transformer.setOutputProperty(OutputKeys.METHOD,"gpx")
    transformer.setOutputProperty(OutputKeys.)
    val saveFolder = File(folderPath) // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val path = "route_${System.currentTimeMillis()}.gpx"
    val file = File(saveFolder, path)         //로컬에 파일저장

    val source = DOMSource(document)
    //val result = StreamResult(FileOutputStream(file))
    val result = StreamResult(System.out)
    transformer.transform(source, result)
    return Uri.fromFile(file)
  }catch(e:Exception){
    e.printStackTrace()
  }*/

/*
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
  }*/
  return Uri.EMPTY
}

fun String.gpxToClass(): RouteGPX {
  /*val gpx = GPX.read(this)
  return RouteGPX("test", "Test", gpx.wayPoints, gpx.tracks[0].segments[0].points)*/
  return RouteGPX(0, "", mutableListOf(), mutableListOf())
}

fun Location.toWayPoint(type: WayPointType): WayPoint {
  return when (type) {
    START_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Start", "Start Point", time, type)
    FINISH_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Finish", "Finish Point", time, type)
    DISTANCE_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Distance", "Distance Point", time, type)
    TURNING_LEFT_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Turning Point", "Turn Left", time, type)
    TURNING_RIGHT_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Turning Point", "Turn Right", time, type)
    TRACK_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Track Point", "Track Point", time, type)
  }
}