package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState

abstract class TraceMap {
    abstract fun display(location: Location)
   fun start(){
       userState=UserState.RUNNING
   }
   fun pause(){
       privacy=Privacy.PUBLIC
       userState=UserState.PAUSED
   }
   fun restart(){
       userState=UserState.RUNNING
   }
   fun stop(){
       userState=UserState.STOP
   }
    var mMap: GoogleMap? = null    //racingMap 인스턴스

    var TAG = "TraceMap"       //로그용 태그
    var privacy=Privacy.RACING
    var distance=0.0
    var time=0.0
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    var currentLocation: LatLng = LatLng(37.619742, 127.060836)              //현재위치
    var userState: UserState = UserState.NORMAL       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
}