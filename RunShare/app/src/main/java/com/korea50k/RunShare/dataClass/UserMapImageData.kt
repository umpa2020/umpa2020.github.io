package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserMapImageData() : Serializable {

    @SerializedName("MapImage")
    @Expose
    var usermapimage: String = ""

    @SerializedName("MapTitle")
    @Expose
    var usermaptitle: String = ""
}