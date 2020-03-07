package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class testdata(
    //var latlngs: ArrayList<ArrayList<LatLng>> = arrayListOf()
    var index: Int? = 0,
    var nearMap: NearMap = NearMap("ss", 0.0)
) : Parcelable