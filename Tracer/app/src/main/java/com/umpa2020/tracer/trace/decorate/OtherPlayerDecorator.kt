package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.UserState

class OtherPlayerDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
  override fun work(location: Location) {
    super.work(location)
    Log.d(TAG,"is it working?")
    when (userState) {
      UserState.RUNNING -> {
        //if(moving) calcDistance()
      }
    }
  }
}