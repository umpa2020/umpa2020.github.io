package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RankDetailMapData() : Serializable {
    @SerializedName("ChallengerId")
    @Expose
    var ChallengerId: String = "0"

    @SerializedName("ChallengerIdTime")
    @Expose
    var ChallengerTime: String = "0"
}