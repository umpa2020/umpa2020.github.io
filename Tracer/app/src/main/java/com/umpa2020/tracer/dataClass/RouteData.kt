package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteData(
    var altitude: List<Double> = listOf(.0), // 고도
    var latlngs: MutableList<MutableList<LatLng>> = mutableListOf(),
    var markerlatlngs: MutableList<LatLng> = mutableListOf()
) : Parcelable