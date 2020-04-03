package com.umpa2020.tracer.extensions

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import io.jenetics.jpx.WayPoint

fun Location.toLatLng():LatLng{
  return LatLng(latitude.toDouble(), longitude.toDouble())
}
fun WayPoint.toLatLng(): LatLng {
  return LatLng(latitude.toDouble(), longitude.toDouble())
}