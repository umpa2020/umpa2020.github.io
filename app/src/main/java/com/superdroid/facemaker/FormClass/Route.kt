package com.superdroid.facemaker.FormClass

import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.hours

//경로, 시간, 거리
class Route : Serializable {
    var route=""
    var distance =0.0
    var time=0
    lateinit var imgFile:File   //루트 썸네일
}