package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RankingData(
  val makerNickname: String? = null,
  val challengerNickname: String? = null,
  val challengerTime: Long? = null,
  var bestTime: Int? = null
) : Parcelable