package com.umpa2020.tracer.extensions

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.jenetics.jpx.WayPoint

fun Location.toLatLng():LatLng{
  return LatLng(latitude.toDouble(), longitude.toDouble())
}
fun WayPoint.toLatLng(): LatLng {
  return LatLng(latitude.toDouble(), longitude.toDouble())
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
  return LatLngBounds(LatLng(minlat,minlon), LatLng(maxlat,maxlon))
}