package com.korea50k.RunShare.dataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeedMapCommentData() : Serializable {
    //var rank: Int = 0
    @SerializedName("UserImage") // 이미지
    @Expose
    var UserImage: String = ""

    @SerializedName("UserId")
    @Expose
    var UserId: String = "김정현"


    @SerializedName("Comment")
    @Expose
    var MapComment: String = "e123123123"


}