package com.korea50k.RunShare

import android.telecom.Call
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// 이거는 바탕화면에 옮겨놓은거
interface API {
    @POST ("signUp.php")
    @FormUrlEncoded
    fun signUp(@Field("Id") Id : String, @Field("Password") Password : String, @Field("Name") Name : String) : retrofit2.Call<ResponseBody>

    @POST ("logIn.php")
    @FormUrlEncoded
    fun logIn(@Field("Id") Id : String, @Field("Password") Password : String) : retrofit2.Call<ResponseBody>

    @POST ("JsonUpload.php")
    @FormUrlEncoded
    fun JsonUpload(@Field("Id") Id : String, @Field("Json") Json : String, @Field("Status") Status : Int) : retrofit2.Call<ResponseBody>

    @POST ("s3Upload.php")
    @FormUrlEncoded
    fun s3Upload(@Field("Id") Id : String, @Field("MapTitle") MapTitle: String, @Field("MapDescription") MapDescription : String,
                 @Field("MapJson") MapJson : String, @Field("MapImage") MapImage : String, @Field("Kcal") Kcal: String,
                 @Field("Distance") Distance : String, @Field("Velocity") Velocity : String, @Field("Time") Time: String,
                 @Field("Excute") Excute : Int, @Field("Likes") Likes : Int, @Field("Status") Status: Int) : retrofit2.Call<ResponseBody>



    @POST ("dbDownloadtest.php")
    @FormUrlEncoded
    fun dbDownloadtest(@Field("Id") Id : String) : retrofit2.Call<ResponseBody>

    @POST ("dbDownloadtest.php")
    @FormUrlEncoded
    fun test(@Field("dynamicRequest") Fields : String ) : retrofit2.Call<ResponseBody>

    @POST ("rankDownload.php")
    @FormUrlEncoded
    fun rankDownload(@Field("Id") Id : String) : retrofit2.Call<ResponseBody>
}