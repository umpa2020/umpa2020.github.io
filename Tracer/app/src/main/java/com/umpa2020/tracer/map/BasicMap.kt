package com.umpa2020.tracer.map

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.dataClass.UserState

class BasicMap//객체 생성자
//맵프레그먼트와 연결
//Running
    (smf: SupportMapFragment, var context: Context) : OnMapReadyCallback {
    var mMap: GoogleMap? = null    //racingMap 인스턴스

    var TAG = "BasicMap"       //로그용 태그
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    var currentLocation: LatLng = LatLng(0.0, 0.0)              //현재위치
    var userState: UserState       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var cameraFlag = false

    init {
        userState = UserState.NORMAL
        smf.getMapAsync(this)
    }

    var lastLocat : Location? = null
    override fun onMapReady(googleMap: GoogleMap) { //after the map is loaded

        mMap = googleMap //구글맵
        mMap!!.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    }

    fun setLocation(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        currentLocation = LatLng(lat, lng)

        if(!cameraFlag) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
            cameraFlag = true
        }
        previousLocation = currentLocation                              //현재위치를 이전위치로 변경
    }
}