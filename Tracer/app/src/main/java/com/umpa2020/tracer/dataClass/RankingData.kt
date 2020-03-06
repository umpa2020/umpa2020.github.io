package com.umpa2020.tracer.dataClass

data class RankingData(
    val makerNickname: String? = null,
    val challengerNickname: String? = null,
    val challengerTime: Long? = null,
    var bestTime: Int? = null
)