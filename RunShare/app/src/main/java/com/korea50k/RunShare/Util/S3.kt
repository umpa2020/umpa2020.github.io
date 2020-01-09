package com.korea50k.RunShare.Util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.korea50k.RunShare.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL

class S3{
    companion object{
        fun downloadBitmap(link:String): Bitmap {
            val url = URL(link)
            val conn = url.openConnection()
            conn.connect()
            val bis = BufferedInputStream(conn.getInputStream())
            var bm = BitmapFactory.decodeStream(bis)
            bis.close()
            return bm
        }

        fun uploadBitmap(bitmap:Bitmap,link: String){
            //Bitmap to string
            var byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            var byteArray = byteArrayOutputStream.toByteArray()
            var base64OfBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT)

            RetrofitClient.retrofitService.uploadImage(
                base64OfBitmap,link
            ).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    val result: String? = response.body().toString()
                    Log.d("Tag","json 업로드 성공" + result)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("ssmm11", t.message);
                    t.printStackTrace()
                }
            })
        }
    }
}