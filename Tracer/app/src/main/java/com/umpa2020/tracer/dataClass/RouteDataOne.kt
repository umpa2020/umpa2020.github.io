package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteDataOne(
    var altitude: List<Double> = listOf(.0), // 고도
    var markerlatlngs: MutableList<LatLng> = mutableListOf()
) : Parcelable