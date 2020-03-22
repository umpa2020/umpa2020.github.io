package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.RouteGPX
import io.jenetics.jpx.WayPoint
import java.lang.StringBuilder

interface TraceMap {
    fun work(location: Location)
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
   fun stop():RouteGPX{
       userState=UserState.STOP
       return RouteGPX("","",wpList,tpList)
   }
    var mMap: GoogleMap
    var testString: String
    var TAG :String      //로그용 태그
    var privacy:Privacy
    var distance:Double
    var time:Double
    var previousLocation: LatLng          //이전위치
    var currentLocation: LatLng           //현재위치
    var altitude:Double
    var speed:Double
    var userState: UserState     //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var moving:Boolean      //사용자가 현재 움직이는 중인지
    var tpList: MutableList<WayPoint>   //track point list
    var wpList: MutableList<WayPoint>   //way point list

}