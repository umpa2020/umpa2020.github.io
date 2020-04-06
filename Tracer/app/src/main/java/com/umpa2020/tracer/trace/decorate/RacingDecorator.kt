package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.Constants.Companion.ARRIVE_BOUNDARY
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION_DISTANCE
import com.umpa2020.tracer.constant.Constants.Companion.INFOUPDATE
import com.umpa2020.tracer.constant.Constants.Companion.RACINGFINISH
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.trace.MyHandler
import com.umpa2020.tracer.util.Logg

class RacingDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
  var myHandler = MyHandler.myHandler!!
  var deviationFlag = false
  var deviationCount = 0
  override fun work(location: Location) {
    super.work(location)
    myHandler.sendEmptyMessage(INFOUPDATE)
    when (userState) {
      UserState.NORMAL -> {
        Logg.d("NORMAL")
        checkIsReady()
      }
      UserState.READYTORACING -> {
        Logg.d("READYTORACING")
        checkIsReadyToRacing()
      }
      UserState.RUNNING -> {
        Logg.d("RUNNING")
        checkMarker()
        checkDeviation()
      }
      else -> {
      }
    }
  }

  private fun checkMarker() {
    if (SphericalUtil.computeDistanceBetween(
        currentLocation,
        markerList[nextWP].position
      ) < ARRIVE_BOUNDARY
    ) {
      markerList[nextWP].remove()
      markerList[nextWP] = mMap.addMarker(
        MarkerOptions()
          .position(markerList[nextWP].position)
          .title(markerList[nextWP].title)
          .icon(
            BitmapDescriptorFactory
              .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
          )
      )
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
        DEVIATION_DISTANCE
      )
    ) {//경로 안에 있으면
      if (deviationFlag) {
        deviationFlag = false
        deviationCount = 0
      }
    } else {//경로 이탈이면
      deviationCount++
      myHandler.sendMessage(myHandler.obtainMessage(DEVIATION, deviationCount))
    }
  }

  private fun checkIsReadyToRacing() {
    if (SphericalUtil.computeDistanceBetween(
        currentLocation,
        LatLng(routeGPX!!.wptList[0].latitude.toDouble(), routeGPX!!.wptList[0].longitude.toDouble())
      ) > ARRIVE_BOUNDARY
    ) {
      userState = UserState.BEFORERACING
      markerList.first().remove()
      markerList[0] = mMap.addMarker(MarkerOptions()
        .position(markerList.first().position)
        .title(markerList.first().title)
        .icon( BitmapDescriptorFactory
          .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)))
    } else {
      markerList.first().remove()
      markerList[0] = mMap.addMarker(MarkerOptions()
        .position(markerList.first().position)
        .title(markerList.first().title)
        .icon( BitmapDescriptorFactory
          .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

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
    Logg.d(
      SphericalUtil.computeDistanceBetween(
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