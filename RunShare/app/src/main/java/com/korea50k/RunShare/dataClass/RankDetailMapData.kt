package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RankDetailMapData() : Serializable {
    //var rank: Int = 0
    @SerializedName("MapTitle")
    @Expose
    var MapTitle: String = ""

    @SerializedName("ChallengerId")
    @Expose
    var ChallengerId: String = "0"

    @SerializedName("ChallengerIdTime")
    @Expose
    var ChallengerTime: String = "0"

    @SerializedName("TempData")
    @Expose
    var TempData: String = ""

    @SerializedName("Id")
    @Expose
    var Id: String = ""

}