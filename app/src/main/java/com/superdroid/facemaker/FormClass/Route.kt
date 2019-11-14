package com.superdroid.facemaker.FormClass

import com.google.android.gms.maps.model.LatLng
import kotlin.time.ExperimentalTime
import kotlin.time.hours

//경로, 시간, 거리
class Route{
    var route=ArrayList<LatLng>()
    var distance =0.0
    var time=0
}