package com.korea50k.tracer.dataClass

data class RankingData(
    val nickname: String? = null,
    val mapTitle: String? = null,
    val mapExplanation: String? = null,
    val mapJson: String? = null,
    val mapImage: String? = null,
    val distance: String? = null,
    val time: String? = null, // Time 형식 어떻게 할지
    val execute: Int? = null,
    val likes: Int? = null,
    val privacy: String? = null
)