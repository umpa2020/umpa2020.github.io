package com.umpa2020.tracer.main.start

import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

open class BaseRunningActivity : AppCompatActivity(), OnMapReadyCallback {
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
  var markerCount = 1

  private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

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

  fun setLocation(location: Location): Boolean {//현재위치를 이전위치로 변경
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    locationBroadcastReceiver = LocationBroadcastReceiver(this)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBroadcastReceiver)
  }

  override fun onResume() {
    super.onResume()
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))
  }

  lateinit var noticePopup: ChoicePopup
  override fun onBackPressed() {
    when (userState) {
      UserState.RUNNING, UserState.PAUSED -> {
        noticePopup = ChoicePopup(this, "선택해주세요.",
          "지금 정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
          "예", "아니오",
          View.OnClickListener {
            // yes 버튼 눌렀을 때 해당 액티비티 재시작.
            finish()
          },
          View.OnClickListener {
            noticePopup!!.dismiss()
          })
        noticePopup!!.show()
      }
      else -> {
        super.onBackPressed()
      }
    }
  }
}
