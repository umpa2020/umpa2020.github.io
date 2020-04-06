package com.umpa2020.tracer.trace

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.extensions.getMinMax
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

class TraceMap(val mMap: GoogleMap) {

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  lateinit var loadTrack: Polyline
  var markerList = mutableListOf<Marker>()
  fun drawRoute(track: MutableList<LatLng>, wptList: MutableList<WayPoint>) {
    Logg.d("Map is draw")
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    wptList.forEachIndexed { i, it ->
      val icon = when (i) {
        0 -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        }
        wptList.size - 1 -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
        else -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

        }
      }

      markerList.add(
        mMap.addMarker(
          MarkerOptions()
            .position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
            .title(it.name.toString())
            .icon(icon)
        )
      )
    }
    val trackMinMax = track.getMinMax()
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(trackMinMax.first, trackMinMax.second), 1080, 300, 100))
    Logg.d(trackMinMax.first.toString() + trackMinMax.second.toString())
  }

  fun drawPolyLine(preLoc: LatLng, curLoc: LatLng) {
    Logg.d("making polyline $preLoc $curLoc")
    //polyline 그리기
    mMap.addPolyline(
      PolylineOptions().add(
        preLoc,
        curLoc
      )
    )
  }
  fun drawMarker(){

  }
  fun moveCamera(latlng: LatLng) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17F))
  }

  fun changeMarkerColor(nextWP: Int,color:Float) {
    markerList[nextWP].remove()
    markerList[nextWP] = mMap.addMarker(
      MarkerOptions()
        .position(markerList[nextWP].position)
        .title(markerList[nextWP].title)
        .icon(
          BitmapDescriptorFactory
            .defaultMarker(color)
        )
    )
  }
}