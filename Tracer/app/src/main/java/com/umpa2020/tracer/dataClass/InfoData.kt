package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.umpa2020.tracer.constant.Privacy
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoData(
  var mapId: String? = null,
  var mapTitle: String? = null,
  var makerId: String? = null,
  var mapExplanation: String? = null,
  var mapImagePath: String? = null,
  var routeGPXPath: String? = null,
  var distance: Double? = null,
  var time: Long? = null,
  var plays: Int? = null,
  var likes: Int? = null,
  var startLatitude: Double? = null,
  var startLongitude: Double? = null,
  var isLiked: Boolean = false,
  var isPlayed: Boolean = false,
  var averageSpeed: Double? = null,
  var maxSpeed: Double? = null
) : Parcelable
