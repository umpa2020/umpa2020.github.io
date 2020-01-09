package com.korea50k.RunShare.dataClass

import java.io.Serializable

//경로, 시간, 거리
class RunningData() : Serializable {
    var lats: DoubleArray= DoubleArray(0)
    var lngs: DoubleArray= DoubleArray(0)
    var alts: DoubleArray=DoubleArray(0)
    var speed: DoubleArray=DoubleArray(0)
    var distance: String = ""
    var time: String = ""
    var mapTitle: String = ""
    var mapExplanation:String=""
    var thumbnail: String=""
    var json:String=""
    var privacy:Privacy=Privacy.PUBLIC
}