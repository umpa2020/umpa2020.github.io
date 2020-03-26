package com.umpa2020.tracer.trace.decorate

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

class BasicMap(val smf: SupportMapFragment, val context: Context) : OnMapReadyCallback, TraceMap {
    override var routeGPX:RouteGPX?=null
    override var wayPoint:MutableList<Marker> = mutableListOf()
    override var track:MutableList<LatLng> = mutableListOf()
    override var nextWP:Int=0
    override lateinit var loadTrack: Polyline
    override lateinit var mMap: GoogleMap
    override var testString: String = ""
    override var TAG = "TraceMap"       //로그용 태그
    override var privacy = Privacy.RACING
    override var distance = 0.0
    override var time = 0.0
    override var previousLocation = LatLng(0.0, 0.0)          //이전위치
    override var currentLocation = LatLng(37.619742, 127.060836)              //현재위치
    override var elevation=0.0
    override var speed=0.0
    override var userState = UserState.NORMAL       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    override var moving = false
    override var trkList: MutableList<WayPoint> = mutableListOf()
    override var wpList: MutableList<WayPoint> = mutableListOf()
    override fun work(location: Location) {
        setLocation(location)
    }
    init {
        smf.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { //after the traceMap is loaded
        Logg.d( "onMapReady")
        mMap = googleMap //구글맵
        mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
        if(routeGPX!=null){
            nextWP=1
            draw()
        }

    }

    private fun setLocation(location: Location) {//현재위치를 이전위치로 변경
        val lat = location.latitude
        val lng = location.longitude
        elevation=location.altitude
        speed=location.speed.toDouble()
        previousLocation = currentLocation
        currentLocation = LatLng(lat, lng)
        if (previousLocation.latitude == currentLocation.latitude
            && previousLocation.longitude == currentLocation.longitude
        ) {
            moving = false
        } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
        } else {
            moving = true
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
        }
    }
}