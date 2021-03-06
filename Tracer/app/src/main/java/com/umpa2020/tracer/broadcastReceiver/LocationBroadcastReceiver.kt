package com.umpa2020.tracer.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.extensions.show
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.lockscreen.viewModel.LocationViewModel
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.util.UserInfo

/**
 *  브로드 케스트 리시버 클래스
 *  serevice로부터 신호(GPS)를 받아 인자값으로 들어오는 map에 전달.
 */
class LocationBroadcastReceiver(val activity: BaseRunningActivity) : BroadcastReceiver() {
  var previousLatLng = LatLng(0.0, 0.0)          //이전위
  var currentLatLng = LatLng(0.0, 0.0)
  var previousTime = 0L
  var currentTime = 0L
  var flag = true
  var currentLocation = Location(LocationManager.GPS_PROVIDER).apply {
    latitude = UserInfo.lat.toDouble()
    longitude = UserInfo.lng.toDouble()
  }
  var bounceTime = 0
  private lateinit var model: LocationViewModel

  override fun onReceive(context: Context?, intent: Intent?) {
    val message = intent?.getParcelableExtra<Location>("message")
    currentLocation = message as Location
    currentLatLng = currentLocation.toLatLng()
    currentTime = SystemClock.elapsedRealtime()

    if (flag) { // 맨 처음엔 이전 위치가 없으므로

      activity.updateLocation(currentLocation) // currentLocation : Location
      previousLatLng = currentLatLng
      previousTime = currentTime
      flag = false
    } else {
      if ((((currentTime - previousTime) / 1000) + 1) * 10
        > SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng)
      ) {
        activity.updateLocation(currentLocation)
        previousLatLng = currentLatLng
        previousTime = currentTime
        bounceTime = 0

      } else {

        bounceTime++
        if (bounceTime > 10) {
          "gps가 오랜시간 동안 불안정 합니다. gps 상태를 확인해 주세요".show()
        }
      }
    }
  }
}
