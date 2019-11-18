package com.superdroid.facemaker.FormClass

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.Serializable
import java.nio.file.Path
import kotlin.time.ExperimentalTime
import kotlin.time.hours

//경로, 시간, 거리
class Route : Serializable {
    var route=""
    var distance =0.0
    var time=0
    var id=""
    lateinit var bitmap: String   //루트 썸네일
}