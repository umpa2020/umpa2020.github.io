package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProfileData() : Serializable {
    @SerializedName("MapImage") // 이미지
    @Expose
    var MapImage: String = ""

    @SerializedName("MapTitle")
    @Expose
    var MapTitle: String = ""
}