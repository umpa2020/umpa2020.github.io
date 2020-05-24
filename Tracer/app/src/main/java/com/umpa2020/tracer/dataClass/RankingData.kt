package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RankingData(
  val makerId: String? = null,
  val challengerId: String? = null,
  val challengerNickname: String? = null,
  val challengerTime: Long? = null,
  var BestTime: Boolean = false,
  val maxSpeed: Double? = null,
  val averageSpeed: Double? = null,
  var racerGPX: String? = null
) : Parcelable