package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RankMapData() : Serializable {
    //var rank: Int = 0
    @SerializedName("MapTitle")
    @Expose
    var mapTitle: String = ""

    @SerializedName("Excute")
    @Expose
    var excute: String = "0"

    @SerializedName("Likes")
    @Expose
    var likes: String = "0"

    @SerializedName("MapImage")
    @Expose
    var mapImage: String = ""

    @SerializedName("Id")
    @Expose
    var id: String = ""

}