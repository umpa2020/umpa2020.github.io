package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.Constants.Companion.INFOUPDATE
import com.umpa2020.tracer.constant.Constants.Companion.RACINGFINISH
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.util.MyHandler

class RacingDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
  var myHandler = MyHandler.myHandler!!
  override fun work(location: Location) {
    super.work(location)
    Log.d(TAG, "RacingDecorator")
    myHandler.sendEmptyMessage(INFOUPDATE)
    when (userState) {
      UserState.NORMAL -> {
        Log.d(TAG, "NORMAL")
        checkIsReady()
      }
      UserState.READYTORACING -> {
        Log.d(TAG, "READYTORACING")
        checkIsReadyToRacing()
      }
      UserState.RUNNING -> {
        Log.d(TAG, "RUNNING")
        checkMarker()
        checkDeviation()
      }
    }
  }

  private fun checkMarker() {
    if (SphericalUtil.computeDistanceBetween(
        currentLocation,
        wayPoint[nextWP].position
      ) < 10
    ) {
      wayPoint[nextWP].remove()
      wayPoint[nextWP]=mMap.addMarker(
        MarkerOptions()
          .position(wayPoint[nextWP].position)
          .title(wayPoint[nextWP].title)
          .icon(
            BitmapDescriptorFactory
              .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
      nextWP++
      if (nextWP == routeGPX!!.wptList.size) {
        myHandler.sendEmptyMessage(RACINGFINISH)
      }
    }
  }

  private fun checkDeviation() {
    //경로이탈검사
    if (PolyUtil.isLocationOnPath(
        currentLocation,
        track,
        false,
        20.0
      )
    ) {//경로 안에 있으면
      /*
      if (manageRacing.noticeState == NoticeState.DEVIATION) {
          manageRacing.noticeState = NoticeState.NOTHING
          countDeviation = 0
          manageRacing.deviation(countDeviation)
      }*/
    } else {//경로 이탈이면
      /*
      manageRacing.deviation(++countDeviation)
      if (countDeviation > 30) {
          manageRacing.stopRacing(false)
      }*/

    }
  }

  private fun checkIsReadyToRacing() {
    if (SphericalUtil.computeDistanceBetween(
        currentLocation,
        LatLng(routeGPX!!.wptList[0].latitude.toDouble(), routeGPX!!.wptList[0].longitude.toDouble())
      ) > 10
    ) {
      userState = UserState.BEFORERACING
    } else {
      /*(context as Activity).runOnUiThread(Runnable {
          (context as Activity).racingNotificationButton.text =
              "시작을 원하시면 START를 누르세요"
      })*/
    }
  }

  private fun checkIsReady() {
    /*
    (context as Activity).runOnUiThread(Runnable {
        (context as Activity).racingNotificationButton.text =
            ("시작 포인트로 이동하십시오.\n시작포인트까지 남은거리\n"
                    + (SphericalUtil.computeDistanceBetween(
                currentLocation,
                markers[0]
            )).roundToLong().toString() + "m")
    })*/
    //시작포인트에 10m이내로 들어오면 준비상태로 변경
    Log.d(
      TAG, SphericalUtil.computeDistanceBetween(
        currentLocation,
        LatLng(routeGPX!!.wptList[0].latitude.toDouble(), routeGPX!!.wptList[0].longitude.toDouble())
      ).toString()
    )
    if (SphericalUtil.computeDistanceBetween(
        currentLocation,
        LatLng(routeGPX!!.wptList[0].latitude.toDouble(), routeGPX!!.wptList[0].longitude.toDouble())
      ) <= 10
    ) {
      userState = UserState.READYTORACING
    }
  }

  private fun calcDistance() {
    distance += SphericalUtil.computeDistanceBetween(previousLocation, currentLocation)
    /*TODO:context by App instance
    context.runOnUiThread {
        (context as Activity).findViewById<TextView>(R.id.runningDistanceTextView).text=distance.toString()
    }*/
  }
}