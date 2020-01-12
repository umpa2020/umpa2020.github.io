package com.korea50k.RunShare.dataClass

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserMapImageData() : Serializable {
    @SerializedName("MapTitle")
    @Expose
    var MapTitle: String = ""

    @SerializedName("MapImage")
    @Expose
    var MapImage: String = ""

    @SerializedName("MapImage")
    @Expose
    lateinit var bitmap: Bitmap
}