package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RankMapData() : Serializable {
    //var rank: Int = 0
    @SerializedName("MapTitle")
    @Expose
    var MapTitle: String = ""

    @SerializedName("Excute")
    @Expose
    var Excute: String = "0"

    @SerializedName("Likes")
    @Expose
    var Likes: String = "0"

    @SerializedName("MapImage")
    @Expose
    var MapImage: String = ""

}