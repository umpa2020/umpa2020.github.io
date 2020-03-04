package com.umpa2020.tracer.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

object testmap : OnMapReadyCallback {

    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "WSY"       //로그용 태그

    init {
//        userState = UserState.RUNNING
    }

    override fun onMapReady(p0: GoogleMap?) {
    }
}