package com.korea50k.RunShare.Activities.Profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.S3
import com.korea50k.RunShare.Util.SharedPreValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_information.*
import kotlinx.android.synthetic.main.activity_my_information.profileImage
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_rank.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL

class MyInformationActivity : AppCompatActivity() {
    val WSY = "WSY"
    var count = false

    // 카메라 requestCode
    private val PICK_FROM_ALBUM = 1
    private var bitmapImg : Bitmap? = null

    var userEmail : String? = null
    var userPW : String? = null
    var imageUri : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my_information)
        userEmail = SharedPreValue.getEMAILData(this)
        userPW = SharedPreValue.getPWDData(this)

        my_information_email_textview.text = userEmail
        my_information_nickname_textview.text = SharedPreValue.getNicknameData(this)
        my_information_age_textview.text = SharedPreValue.getAgeData(this)
        my_information_sex_textview.text = SharedPreValue.getGenderData(this)
        Log.d("info", "nickname" + SharedPreValue.getNicknameData(this))
        Log.d("info", "my_information_age_textview" + SharedPreValue.getAgeData(this))
        Log.d("info", "my_information_sex_textview" + SharedPreValue.getGenderData(this))

        imageUri = SharedPreValue.getProfileData(this)
        Log.d(WSY,imageUri)

        class SetImageTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()

            }
            var bm: Bitmap? = null

            override fun doInBackground(vararg params: Void?): String? {
                try {

                    bm = S3.downloadBitmap(imageUri!!)

                } catch (e : java.lang.Exception) {
                    Log.d(WSY, "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
                profileImage.setImageBitmap(bm)
            }
        }
        var Start = SetImageTask()
        Start.execute()
    }

    private fun goToAlbum() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    var options : BitmapFactory.Options? = null
    var resized : Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        // 앨범
        if (requestCode == PICK_FROM_ALBUM) {
//
            if(resultCode == RESULT_OK)
            {
                try{
                    var inputStream =
                        intentData!!.data?.let { getContentResolver().openInputStream(it) }

                    // 프로필 사진을 비트맵으로 변환
                    options = BitmapFactory.Options()
                    options!!.inSampleSize = 2
                    bitmapImg = BitmapFactory.decodeStream(inputStream,null, options)
                    inputStream!!.close()
                    Log.d(WSY, bitmapImg.toString())

                    resized = Bitmap.createScaledBitmap(bitmapImg!!,bitmapImg!!.width, bitmapImg!!.height, true)
                    profileImage.setImageBitmap(resized)
                    profileImage.scaleType = ImageView.ScaleType.CENTER_CROP

                    // 이미지가 바뀌면 서버에 전송.
                    if(bitmapImg != null) {
                        // 바뀌 프로필 비트맵을 base640으로 변경.
                        var byteArrayOutputStream = ByteArrayOutputStream()
                        resized!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        var byteArray = byteArrayOutputStream.toByteArray()
                        var base64OfBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT)

                        class dowmloadImage : AsyncTask<Void, Void, String>(){
                            override fun onPreExecute() {
                                super.onPreExecute()
                               // my_information_adjust_button.isEnabled = false
                            }

                            override fun doInBackground(vararg params: Void?): String? {
                                try {
                                    RetrofitClient.retrofitService.changeProfile( userEmail!!, userPW!!, base64OfBitmap!!
                                    ).enqueue(object : retrofit2.Callback<ResponseBody> {
                                        override fun onResponse( call: Call<ResponseBody>, response: Response<ResponseBody>
                                        ) {
                                            val result = response.body() as ResponseBody
                                            val resultValue = result.string()

                                            Log.i(WSY, "결과 : " + resultValue)
                                            var userData = JSONObject(resultValue)


                                            // SharedProfile 재 저장
                                            SharedPreValue.setProfileData(
                                                this@MyInformationActivity,
                                                userData.getString("ProfilePath")
                                            )
                                            Log.d(WSY, SharedPreValue.getProfileData(applicationContext))
                                        }

                                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                                        }
                                    })
                                } catch (e : java.lang.Exception) {
                                    Log.d(WSY, "이미지 다운로드 실패 " +e.toString())
                                }
                                return null
                            }

                            override fun onPostExecute(result: String?) {
                                super.onPostExecute(result)
                                //TODO:피드에서 이미지 적용해볼 소스코드
                              //  my_information_adjust_button.isEnabled = true
                            }
                        }
                        var Start = dowmloadImage ()
                        Start.execute()

                    }
                    //TODO:서버로 데이터 전송
                }catch(e : java.lang.Exception)
                {

                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                //사진 선택 취소
               // Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    var buttonText : String? = null
    fun onClick(v: View) {
        when (v.id) {
            R.id.profileImage -> {
//                buttonText = my_information_adjust_button.text as String?
//                if(buttonText.equals("확인"))
                    goToAlbum()
            }
//            R.id.my_information_adjust_button ->
//            { // 수정 버튼 클릭 시
//                buttonText = my_information_adjust_button.text as String?
//                Log.d(WSY,buttonText)
//
//                if(buttonText.equals("수정")){
//                    my_information_adjust_button.text = "확인"
//                    editProfile.visibility = View.VISIBLE
//
//                }else if(buttonText.equals("확인")){
//                    my_information_adjust_button.text = "수정"
//                    editProfile.visibility = View.INVISIBLE
//                    // 서버에 바뀐 프로필 setting
//                    Log.d(WSY, userEmail!! + "\n" + userPW!! + "\n"+  imageUri!!)
//            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(WSY,"onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(WSY, SharedPreValue.getProfileData(this))
        Log.d(WSY, "onDestroy()")
    }
}
