package com.umpa2020.tracer.trace

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState

class LocationManager private constructor() {
    companion object {
        private var outInstance: LocationManager? = null
        val instance: LocationManager
            get() {
                if (outInstance == null) outInstance = LocationManager()
                return outInstance!!
            }
    }
    lateinit var mMap: GoogleMap
    var privacy= Privacy.RACING
    var distance=0.0
    var time=0.0
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    var currentLocation: LatLng = LatLng(37.619742, 127.060836)              //현재위치
    var userState: UserState = UserState.NORMAL       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
}
