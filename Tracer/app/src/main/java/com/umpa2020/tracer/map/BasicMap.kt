package com.umpa2020.tracer.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.UserState
import com.umpa2020.tracer.locationBackground.LocationUpdatesComponent

open class BasicMap  (smf: SupportMapFragment, open var context: Context) : OnMapReadyCallback {
    var mMap: GoogleMap? = null    //racingMap 인스턴스

    private var TAG = "BasicMap"       //로그용 태그
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    var currentLocation: LatLng = LatLng(37.619754, 127.060885)              //현재위치
    var userState: UserState = UserState.NORMAL       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var cameraFlag = false

    init {
        smf.getMapAsync(this)
    }

    var lastLocat : Location? = null
    override fun onMapReady(googleMap: GoogleMap) { //after the map is loaded
        Log.d(TAG, "onMapReady")

        mMap = googleMap //구글맵
        mMap!!.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
        if(!cameraFlag) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
            cameraFlag = true
        }
    }

    open fun setLocation(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        currentLocation = LatLng(lat, lng)

        if(!cameraFlag) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
            cameraFlag = true
        }
        previousLocation = currentLocation                              //현재위치를 이전위치로 변경
    }

    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}