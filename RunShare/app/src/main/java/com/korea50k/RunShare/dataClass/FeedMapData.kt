package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeedMapData() : Serializable {
    //var rank: Int = 0
    @SerializedName("Image") // 이미지
    @Expose
    var Uimage: String = ""

    @SerializedName("User")
    @Expose
    var Uname: String = "김정현"

    @SerializedName("Map") // 이미지
    @Expose
    var Mimage: String = "0"

    @SerializedName("MapComment")
    @Expose
    var Mcomment: String = "안녕하세요"

    @SerializedName("Heart") // 이미지
    @Expose
    var Heart: String = "0"

    @SerializedName("Count")
    @Expose
    var HCount: String = "0"

}