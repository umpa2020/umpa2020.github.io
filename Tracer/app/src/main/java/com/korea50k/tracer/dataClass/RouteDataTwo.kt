package com.korea50k.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class RouteDataTwo(
    //var latlngs: ArrayList<ArrayList<LatLng>> = arrayListOf()
    var latlngs: MutableList<LatLng> = mutableListOf()
) : Parcelable