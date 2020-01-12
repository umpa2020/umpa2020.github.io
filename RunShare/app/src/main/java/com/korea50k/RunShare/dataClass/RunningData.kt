package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

//경로, 시간, 거리
class RunningData() : Serializable {
    @SerializedName("Lats")
    @Expose
    lateinit var lats: Array<Vector<Double>>

    @SerializedName("Lngs")
    @Expose
    lateinit var lngs: Array<Vector<Double>>

    @SerializedName("Alts")
    @Expose
    var alts: DoubleArray=DoubleArray(0)

    @SerializedName("Speeds")
    @Expose
    var speed: DoubleArray=DoubleArray(0)

    @SerializedName("MarkerLats")
    @Expose
    lateinit var markerLats:Vector<Double>

    @SerializedName("MarkerLngs")
    @Expose
    lateinit var markerLngs: Vector<Double>

    @SerializedName("Distance")
    @Expose
    var distance: Double = 0.0

    @SerializedName("Time")
    @Expose
    var time: Long = 0

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

    @SerializedName("Id")
    @Expose
    var id:String=""

}
