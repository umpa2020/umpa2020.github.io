package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EmblemNameData(
  val name: String? = null
) : Parcelable