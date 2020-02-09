package com.korea50k.tracer.map

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.tracer.dataClass.UserState

object testmap: OnMapReadyCallback {

    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "WSY"       //로그용 태그
    init {
//        userState = UserState.RUNNING
    }
    override fun onMapReady(p0: GoogleMap?) {
    }
}