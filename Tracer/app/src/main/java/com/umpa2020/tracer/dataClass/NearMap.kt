package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearMap(
    var mapTitle: String,
    var distance: Double
): Parcelable