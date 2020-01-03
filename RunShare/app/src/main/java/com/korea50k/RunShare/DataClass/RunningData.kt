package com.korea50k.RunShare.DataClass

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

//경로, 시간, 거리
class RunningData() : Serializable{
    lateinit var lats:DoubleArray
    lateinit var lngs:DoubleArray
    lateinit var alts:DoubleArray
    var speed: String =""
    var distance: String =""
    var time: String =""
    var map_title: String =""
    var cal: String =""

    lateinit var bitmap: String   //루트 썸네일

}