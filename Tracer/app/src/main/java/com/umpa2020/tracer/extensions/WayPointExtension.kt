package com.umpa2020.tracer.extensions

import com.google.android.gms.maps.model.LatLng
import io.jenetics.jpx.WayPoint

fun WayPoint.toLatLng(): LatLng {
  return LatLng(latitude.toDouble(), longitude.toDouble())
}