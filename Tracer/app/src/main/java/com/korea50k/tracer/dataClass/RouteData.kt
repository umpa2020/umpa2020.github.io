package com.korea50k.tracer.dataClass

data class RouteData(
    val Altitude: List<Double> = listOf(.0), // 고도
    val latitude: List<Double> = listOf(.0), // 위도
    val longitude: List<Double> = listOf(.0) // 경도
)