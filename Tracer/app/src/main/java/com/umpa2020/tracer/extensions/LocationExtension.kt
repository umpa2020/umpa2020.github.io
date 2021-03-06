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
import org.w3c.dom.Node
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.*

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
          desc = "${20000 * i},${40000 * i}"
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
            desc = "${20000 * i},${40000 * i}"
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
  val simplifyLatLngs = PolyUtil.simplify(latlngs, 50.0)
  if (wptList.isNullOrEmpty()) wptList = mutableListOf()
  for (i in 1 until simplifyLatLngs.size - 1) {
    val a = simplifyLatLngs[i - 1]
    val b = simplifyLatLngs[i]
    val c = simplifyLatLngs[i + 1]

    val firstVec = LatLng(b.latitude - a.latitude, b.longitude - a.longitude)
    val secondVec = LatLng(c.latitude - b.latitude, c.longitude - b.longitude)

    val x1 = firstVec.longitude
    val y1 = firstVec.latitude
    val x2 = secondVec.longitude
    val y2 = secondVec.latitude
    val bunja = (x1 * y2 - y1 * x2)
    val bunmo = sqrt(x1.pow(2) + y1.pow(2)) * sqrt(x2.pow(2) + y2.pow(2))
    val angle = asin(bunja / bunmo) * 180 / PI

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

fun RouteGPX.classToGpx(folderPath: String): Uri {
  try {
    val document =
      DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .domImplementation.createDocument("test", "gpx", null)
    wptList.forEach { wpt ->
      document.documentElement.appendChild(
        document.createElement("wpt").apply {
          setAttribute("lat", "${wpt.lat}")
          setAttribute("lon", "${wpt.lon}")
          appendChild(document.createElement("ele").apply { appendChild(document.createTextNode("${wpt.alt}")) })
          appendChild(document.createElement("time").apply { appendChild(document.createTextNode("${wpt.time}")) })
          appendChild(document.createElement("name").apply { appendChild(document.createTextNode(wpt.name)) })
          appendChild(document.createElement("desc").apply { appendChild(document.createTextNode(wpt.desc)) })
          appendChild(document.createElement("type").apply { appendChild(document.createTextNode("${wpt.type?.ordinal}")) })
        })
    }

    val trkseg = document.createElement("trkseg")
    trkList.forEach { wpt ->
      trkseg.appendChild(document.createElement("trkpt").apply {
        setAttribute("lat", "${wpt.lat}")
        setAttribute("lon", "${wpt.lon}")
        appendChild(document.createElement("ele").apply { appendChild(document.createTextNode("${wpt.alt}")) })
        appendChild(document.createElement("speed").apply { appendChild(document.createTextNode("${wpt.speed}")) })
        appendChild(document.createElement("type").apply { appendChild(document.createTextNode("${wpt.type?.ordinal}")) })
      })
    }
    document.documentElement.appendChild(document.createElement("trk").appendChild(trkseg))

    val saveFolder = File(folderPath) // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val path = "route_${System.currentTimeMillis()}.gpx"
    val file = File(saveFolder, path)         //로컬에 파일저장

    val transformer = TransformerFactory.newInstance().newTransformer()
    val source = DOMSource(document)
    val result = StreamResult(FileOutputStream(file))
    // val result = StreamResult(System.out)
    transformer.transform(source, result)
    return Uri.fromFile(file)
  } catch (e: Exception) {
    e.printStackTrace()
  }

  return Uri.EMPTY
}

fun Uri.gpxToClass(): RouteGPX {
  val inputStream = FileInputStream(path!!)
  val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
  val wptList = mutableListOf<WayPoint>()
  dom.documentElement.getElementsByTagName("wpt").let {
    for (i in 0 until it.length) {
      wptList.add(
        it.item(i).let { node ->
          node.toWayPoint()
        })
    }
  }
  val trkList = mutableListOf<WayPoint>()
  dom.documentElement.getElementsByTagName("trkpt").let {
    for (i in 0 until it.length) {
      trkList.add(it.item(i).toWayPoint())
    }
  }
  return RouteGPX(0, "", wptList, trkList)
}

fun Node.toWayPoint(): WayPoint {
  val lat = value("lat")!!.toDouble()
  val lng = value("lon")!!.toDouble()
  var ele = 0.0
  var speed = 0.0
  var time = 0L
  var name = ""
  var desc = ""
  var type = TRACK_POINT
  for (i in 0 until childNodes.length) {
    val item = childNodes.item(i)
    when (item.nodeName) {
      "ele" -> {
        item.textContent.toDoubleOrNull()?.let {
          ele = it
        }
      }
      "speed" -> {
        item.textContent.toDoubleOrNull()?.let {
          speed = it
        }
      }
      "time" -> {
        item.textContent.toLongOrNull()?.let {
          time = it
        }
      }
      "name" -> {
        name = item.textContent
      }
      "desc" -> {
        desc = item.textContent
      }
      "type" -> {
        type = values()[item.textContent.toInt()]
      }
      else -> {
      }
    }
  }
  return WayPoint(lat, lng, ele!!, speed, name, desc, time, type)
}

fun Node.value(name: String): String? {
  return attributes.getNamedItem(name)?.nodeValue

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

fun RouteGPX.getSpeed(): MutableList<Double> {
  val speeds = mutableListOf<Double>()
  trkList.forEach {
    speeds.add(it.speed!!)
  }
  return speeds
}