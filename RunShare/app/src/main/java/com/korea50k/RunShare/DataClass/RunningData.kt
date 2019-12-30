package com.korea50k.RunShare.DataClass

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

//경로, 시간, 거리
class RunningData() : Serializable{
    lateinit var lats:DoubleArray
    lateinit var lngs:DoubleArray
    var distance = 0.0
    var time = ""
    var map_title = ""
    var cal=0
    var speed=0.0
    lateinit var bitmap: String   //루트 썸네일

}