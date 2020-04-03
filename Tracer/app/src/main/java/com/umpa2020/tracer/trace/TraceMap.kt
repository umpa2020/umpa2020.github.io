package com.umpa2020.tracer.trace

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.getMinMax
import com.umpa2020.tracer.util.Logg

class TraceMap(smf: SupportMapFragment, context: Context) : OnMapReadyCallback {
  lateinit var mMap: GoogleMap

  init {
    smf.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    mMap = googleMap //구글맵
    mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
  }

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  fun drawRoute(routeGPX: RouteGPX) {
    Logg.d("Map is draw")
    val track = mutableListOf<LatLng>()
    routeGPX.trkList.forEach {
      track.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
    }
    val loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    routeGPX.wptList.forEachIndexed { i, it ->
      val icon = when (i) {
        0 -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        }
        routeGPX.wptList.size - 1 -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
        else -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

        }
      }
      val markerList= mutableListOf<Marker>()
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
}