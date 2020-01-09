package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//경로, 시간, 거리
class RunningData() : Serializable {
    @SerializedName("Lats")
    @Expose
    var lats: DoubleArray= DoubleArray(0)

    @SerializedName("Lngs")
    @Expose
    var lngs: DoubleArray= DoubleArray(0)

    @SerializedName("Alts")
    @Expose
    var alts: DoubleArray=DoubleArray(0)

    @SerializedName("Speeds")
    @Expose
    var speed: DoubleArray=DoubleArray(0)

    @SerializedName("Distance")
    @Expose
    var distance: Double = 0.0

    @SerializedName("Time")
    @Expose
    var time: String = ""

    @SerializedName("MapTitle")
    @Expose
    var mapTitle: String = ""

    @SerializedName("MapExplanation")
    @Expose
    var mapExplanation:String=""

    @SerializedName("MapImage")
    @Expose
    var mapImage: String=""

    @SerializedName("MapJson")
    @Expose
    var mapJson:String=""

    @SerializedName("Privacy")
    @Expose
    var privacy:Privacy=Privacy.PUBLIC
}
