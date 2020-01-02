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
    lateinit var speed: String
    lateinit var distance: String
    lateinit var time: String
    lateinit var map_title: String
    lateinit var cal: String

    lateinit var bitmap: String   //루트 썸네일

}