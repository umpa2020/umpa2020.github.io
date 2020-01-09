package com.korea50k.RunShare

import android.telecom.Call
import com.korea50k.RunShare.dataClass.Privacy
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// 이거는 바탕화면에 옮겨놓은거
interface API {
    @POST("signUp.php")
    @FormUrlEncoded
    fun signUp(@Field("Id") Id: String, @Field("Password") Password: String, @Field("Name") Name: String): retrofit2.Call<ResponseBody>

    @POST("logIn.php")
    @FormUrlEncoded
    fun logIn(@Field("Id") Id: String, @Field("Password") Password: String): retrofit2.Call<ResponseBody>

    @POST("JsonUpload.php")
    @FormUrlEncoded
    fun JsonUpload(@Field("Id") Id: String, @Field("Json") Json: String, @Field("Status") Status: Int): retrofit2.Call<ResponseBody>

    @POST("runningDataUpload.php")
    @FormUrlEncoded
    fun runningDataUpoload(
        @Field("Id") Id: String, @Field("MapTitle") MapTitle: String, @Field("MapExplanation") MapExplanation: String,
        @Field("MapJson") MapJson: String, @Field("MapImage") MapImage: String, @Field("Distance") Distance: Double,
        @Field("Time") Time: String, @Field("Execute") Execute: Int, @Field("Likes") Likes: Int,
        @Field("Privacy") Privacy: Privacy
    ): retrofit2.Call<ResponseBody>


    @POST("dbDownloadtest.php")
    @FormUrlEncoded
    fun dbDownloadtest(@Field("Id") Id: String): retrofit2.Call<ResponseBody>

    @POST("dbDownloadtest.php")
    @FormUrlEncoded
    fun test(@Field("dynamicRequest") Fields: String): retrofit2.Call<ResponseBody>

    @POST("rankDownload.php")
    @FormUrlEncoded
    fun rankDownload(@Field("Id") Id: String): retrofit2.Call<ResponseBody>

    @POST("rankingMapDownload.php")
    @FormUrlEncoded
    fun rankingMapDownload(@Field("MapTitle") MapTitle: String): retrofit2.Call<ResponseBody>

    @POST("feedDownload.php")
    @FormUrlEncoded
    fun feedDownload(@Field("Id") Id: String): retrofit2.Call<ResponseBody>
}