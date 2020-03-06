package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoData(
    var makersNickname: String? = null,
    var makersUserNumber: String? = null,
    var mapTitle: String? = null,
    var mapExplanation: String? = null,
    var mapImage: String? = null,
    var distance: Double? = null,
    var time: Long? = null,
    var execute: Int? = null,
    var likes: Int? = null,
    var privacy: Privacy=Privacy.PUBLIC,
    var speed: MutableList<Double> = mutableListOf(.0) // 순간 속력
) : Parcelable