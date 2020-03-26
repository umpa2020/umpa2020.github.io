package com.umpa2020.tracer.trace.decorate

import android.graphics.Color
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.RouteGPX
import io.jenetics.jpx.WayPoint

interface TraceMap {
    fun work(location: Location)
    fun draw() {
        Log.d(TAG,"Map is draw")
       track=mutableListOf<LatLng>()
        routeGPX!!.trkList.forEach {
            track.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
        }
        loadTrack =
            mMap.addPolyline(
                PolylineOptions()
                    .addAll(track)
                    .color(Color.GREEN)
                    .startCap(RoundCap() as Cap)
                    .endCap(RoundCap())
            )        //경로를 그릴 폴리라인 집합
        routeGPX!!.wptList.forEachIndexed{i, it ->
            wayPoint.add(mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude.toDouble(),it.longitude.toDouble()))
                    .title(i.toString())
            ))
        }
        //TODO:MINMAX 적용
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(track.first(), track.last()), 1080, 300, 50))
    }
    fun captureMapScreen(callback:GoogleMap.SnapshotReadyCallback){
        mMap.snapshot(callback)
    }


    fun start() {
        userState = UserState.RUNNING
    }

    fun pause() {
        privacy = Privacy.PUBLIC
        userState = UserState.PAUSED
    }

    fun restart() {
        userState = UserState.RUNNING
    }

    fun stop(): RouteGPX {
        userState = UserState.STOP
        return RouteGPX("", "", wpList, trkList)
    }


    var mMap: GoogleMap
    var testString: String
    var TAG: String      //로그용 태그
    var privacy: Privacy
    var distance: Double
    var time: Double
    var previousLocation: LatLng          //이전위치
    var currentLocation: LatLng           //현재위치
    var elevation: Double
    var speed: Double
    var userState: UserState     //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var moving: Boolean      //사용자가 현재 움직이는 중인지
    var trkList: MutableList<WayPoint>   //track point list
    var wpList: MutableList<WayPoint>   //way point list


    var routeGPX:RouteGPX?
    var loadTrack:Polyline
    var wayPoint:MutableList<Marker>
    var track:MutableList<LatLng>
    var nextWP:Int
}