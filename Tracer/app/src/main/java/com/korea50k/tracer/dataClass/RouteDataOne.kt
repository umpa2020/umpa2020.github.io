package com.korea50k.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class RouteDataOne(
    var altitude: List<Double> = listOf(.0), // 고도
    var markerlatlngs: MutableList<LatLng> = mutableListOf()
) : Parcelable