package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AchivementData(
  val mapId: String? = null,
  val time: Long? = null,
  val distance: Double? = null,
  val playTime: Long? = null,
  var mode: String? = null
) : Parcelable