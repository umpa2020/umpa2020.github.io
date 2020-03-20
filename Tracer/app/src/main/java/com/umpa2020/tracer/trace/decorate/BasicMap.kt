package com.umpa2020.tracer.trace.decorate

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class BasicMap (val smf: SupportMapFragment, val context: Context):OnMapReadyCallback, TraceMap(){
    override fun display(location: Location) {
        setLocation(location)
    }

    init{
        smf.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { //after the map is loaded
        Log.d(TAG, "onMapReady")
        mMap = googleMap //구글맵
        mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.

    }

    fun setLocation(location: Location) {
        super.testString+="B"
        Log.d(TAG, super.testString)
        val lat = location.latitude
        val lng = location.longitude
        currentLocation = LatLng(lat, lng)
        previousLocation = currentLocation                              //현재위치를 이전위치로 변경
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
    }
}