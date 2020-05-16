package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AchievementData(
  val mapId: String? = null,
  val ranking: Long? = null
) : Parcelable