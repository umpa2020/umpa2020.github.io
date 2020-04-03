package com.umpa2020.tracer.trace

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.util.Logg

class TraceMap( smf: SupportMapFragment, context: Context): OnMapReadyCallback {
  lateinit var mMap: GoogleMap
  init{
    smf.getMapAsync(this)
  }
  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d( "onMapReady")
    mMap = googleMap //구글맵
    mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
  }
  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }
  fun drawRoute(routeGPX: RouteGPX){

  }

  fun drawPolyLine(preLoc: LatLng, curLoc:LatLng){

  }
}