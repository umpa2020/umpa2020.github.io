package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.umpa2020.tracer.constant.Privacy
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoData(
  var mapId: String = "",
  var mapTitle: String = "",
  var makerId: String = "",
  var mapExplanation: String = "",
  var mapImagePath: String = "",
  var routeGPXPath: String = "",
  var distance: Double = 0.0,
  var time: Long = 0L,
  var plays: Int = 1,
  var likes: Int = 0,
  var startLatitude: Double = 0.0,
  var startLongitude: Double = 0.0,
  var liked: Boolean = false,
  var played: Boolean = false,
  var averageSpeed: Double = 0.0,
  var maxSpeed: Double = 0.0
) : Parcelable
