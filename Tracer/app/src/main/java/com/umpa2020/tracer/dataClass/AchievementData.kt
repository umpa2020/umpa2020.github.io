package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AchievementData(
  val distance: Double = 0.0,
  val plays: Int = 0,
  val trackMake: Int = 0
) : Parcelable