package com.korea50k.RunShare.DataClass

import java.io.Serializable

//경로, 시간, 거리
class Route : Serializable {
    var route = ""
    var distance = 0.0
    var time = ""
    var map_title = ""
    lateinit var bitmap: String   //루트 썸네일
}