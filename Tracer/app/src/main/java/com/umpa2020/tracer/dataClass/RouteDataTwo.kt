package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteDataTwo(
    //var latlngs: ArrayList<ArrayList<LatLng>> = arrayListOf()
    var index: Int? = 0,
    var latlngs: MutableList<LatLng> = mutableListOf()
) : Parcelable