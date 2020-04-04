package com.umpa2020.tracer.main.start

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

open class BaseActivity : AppCompatActivity(), OnMapReadyCallback {
  var routeGPX: RouteGPX? = null
  var markerList: MutableList<Marker> = mutableListOf()
  var track: MutableList<LatLng> = mutableListOf()
  var nextWP: Int = 0
  lateinit var loadTrack: Polyline
  lateinit var traceMap: TraceMap
  var privacy = Privacy.RACING
  var distance = 0.0
  var time = 0.0
  var previousLatLng = LatLng(0.0, 0.0)          //이전위
  var currentLatLng = LatLng(37.619742, 127.060836)
  var elevation = 0.0
  var speed = 0.0
  var userState = UserState.NORMAL       //사용자의 현재상태 달리기
  var moving = false
  var trkList: MutableList<WayPoint> = mutableListOf()
  var wpList: MutableList<WayPoint> = mutableListOf()
  var markerCount = -1

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
     }
  open fun updateLocation(curLoc: Location) {
    if (setLocation(curLoc)) {
      when (userState) {
        UserState.NORMAL -> {
        }
        UserState.READYTORUNNING -> {
        }
        UserState.RUNNING -> {
          distance += SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng)
          traceMap.drawPolyLine(previousLatLng, currentLatLng)
          //tplist에 추가
          trkList.add(
            WayPoint.builder()
              .lat(currentLatLng.latitude)
              .lon(currentLatLng.longitude)
              .ele(elevation)
              .speed(speed)
              .name("track point")
              .build()
          )
        }
        UserState.PAUSED -> {
        }
        UserState.STOP -> {
        }
        UserState.BEFORERACING -> {
        }
        UserState.READYTORACING -> {
        }
      }
    }
  }

  open fun start() {
    userState = UserState.RUNNING
  }

  open fun pause() {
    privacy = Privacy.PUBLIC
    userState = UserState.PAUSED
  }

  open fun restart() {
    userState = UserState.RUNNING
  }

  open fun stop() {
    userState = UserState.STOP
  }

  fun setLocation(location: Location) :Boolean {//현재위치를 이전위치로 변경
    elevation = location.altitude
    speed = location.speed.toDouble()
    previousLatLng = currentLatLng
    currentLatLng = location.toLatLng()
    if (previousLatLng == currentLatLng) {
      moving = false
    } else if (false) { //TODO:비정상적인 움직임일 경우 + finish에 도착한 경우
    } else {
      moving = true
      traceMap.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16F))
    }
    return moving
  }
}
