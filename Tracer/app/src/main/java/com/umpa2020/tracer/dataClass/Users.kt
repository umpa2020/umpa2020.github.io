package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
  var userId: String,
  var nickname: String,
  var birth: String,
  var gender: String,
  var profileImagePath: String,
  var userState: Boolean
) : Parcelable