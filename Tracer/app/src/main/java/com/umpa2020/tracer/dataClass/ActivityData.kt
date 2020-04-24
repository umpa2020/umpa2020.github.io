package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityData(
  var mapTitle: String? = null,
  var time: String? = null,
  var mode: String? = null
) : Parcelable