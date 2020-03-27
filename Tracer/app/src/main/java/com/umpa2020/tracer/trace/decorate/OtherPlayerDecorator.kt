package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.util.Logg

class OtherPlayerDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
  override fun work(location: Location) {
    super.work(location)
    when (userState) {
      UserState.RUNNING -> {
        updateMarker()

      }
    }
  }

  var otherPlayer: Marker? = null
  private fun updateMarker() {
    var index = (time / routeGPX!!.time.toDouble()) * routeGPX!!.trkList.size
    if (otherPlayer != null) {
      otherPlayer!!.remove()
    }
    otherPlayer = mMap.addMarker(
      MarkerOptions()
        .position(routeGPX!!.trkList[index.toInt()].toLatLng())
        .title("Maker")
        .icon(
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        )
    )
  }
}