package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeedCommunityData() : Serializable {
    //var rank: Int = 0
    @SerializedName("MapImage") // 이미지
    @Expose
    var MapImage: String = ""

    @SerializedName("Id")
    @Expose
    var Id: String = "김정현"

    @SerializedName("MapTitle")
    @Expose
    var MapTitle: String = "안녕하세요"

    @SerializedName("Heart") // 이미지
    @Expose
    var Heart: String = "0"

    @SerializedName("Likes")
    @Expose
    var Likes: String = "0"

}