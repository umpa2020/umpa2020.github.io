package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.umpa2020.tracer.network.BaseFB
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityData(
  val mapId: String? = null,
  val time: Long? = null,
  val distance: Double? = null,
  val playTime: Long? = null,
  var mode: BaseFB.ActivityMode? = null
) : Parcelable