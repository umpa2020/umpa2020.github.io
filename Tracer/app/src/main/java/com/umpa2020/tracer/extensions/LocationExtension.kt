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

fun MutableList<LatLng>.getMinMax(): Pair<LatLng, LatLng> {
  var minlat = first().latitude
  var maxlat = first().latitude
  var minlon = first().longitude
  var maxlon = first().longitude
  forEach {
    if (it.latitude < minlat) minlat = it.latitude
    else if (it.latitude > maxlat) maxlat = it.latitude
    if (it.longitude < minlat) minlon = it.longitude
    else if (it.longitude > maxlat) maxlon = it.longitude
  }
  return Pair(LatLng(minlat,minlon), LatLng(maxlat,maxlon))
}