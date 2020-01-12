package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RankDetailMapData() : Serializable {
    @SerializedName("Name")
    @Expose
    var Rank: Int =0

    @SerializedName("Id")
    @Expose
    var Id: String = "0"

    @SerializedName("ChallengerId")
    @Expose
    var ChallengerId: String = "0"

    @SerializedName("ChallengerIdTime")
    @Expose
    var ChallengerTime: String = "0"
}