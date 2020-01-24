package com.korea50k.tracer.dataClass

import com.google.common.collect.Lists

data class InfoData(
    val makersNickname: String? = null,
    val mapTitle: String? = null,
    val mapExplanation: String? = null,
    val mapImage: String? = null,
    val distance: String? = null,
    val time: String? = null, // Time 형식 어떻게 할지
    val execute: Int? = null,
    val likes: Int? = null,
    val privacy: String? = null,
    val speed: List<Double> = listOf(.0) // 순간 속력
)