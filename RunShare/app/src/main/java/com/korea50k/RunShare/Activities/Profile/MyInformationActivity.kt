package com.korea50k.RunShare.Activities.Profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.korea50k.RunShare.R
import com.korea50k.RunShare.Util.S3
import com.korea50k.RunShare.Util.SharedPreValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_information.*
import kotlinx.android.synthetic.main.activity_my_information.profileImage
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_rank.view.*
import java.io.BufferedInputStream
import java.net.URL

class MyInformationActivity : AppCompatActivity() {
    val WSY = "WSY"
    var count = false

    // 카메라 requestCode
    private val PICK_FROM_ALBUM = 1
    private var bitmapImg : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_information)

        my_information_email_textview.text = SharedPreValue.getEMAILData(this)
        my_information_nickname_textview.text = SharedPreValue.getNicknameData(this)
        my_information_age_textview.text = SharedPreValue.getAgeData(this)
        my_information_sex_textview.text = SharedPreValue.getGenderData(this)
        Log.d("info", "nickname" + SharedPreValue.getNicknameData(this))
        Log.d("info", "my_information_age_textview" + SharedPreValue.getAgeData(this))
        Log.d("info", "my_information_sex_textview" + SharedPreValue.getGenderData(this))

        var imageUri = SharedPreValue.getProfileData(this)
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

        val adjustBtn = findViewById<View>(com.korea50k.RunShare.R.id.my_information_adjust_button) as Button
        adjustBtn.setOnClickListener{
            if(count)
                count = false
            else
                count = true

            if(count == true){ // 각 조건에 따라 버튼 텍스트 변경
                my_information_adjust_button.text = "확인"
                //TODO 프로필 사진 바꾸는 부분 코드 추가
            }
            else{
                my_information_adjust_button.text = "수정"
            }
        }
    }

    private fun goToAlbum() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

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
                    bitmapImg = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()
                    Log.d(WSY, bitmapImg.toString())
                    profileImage.setImageBitmap(bitmapImg)
                    profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    //TODO:서버로 데이터 전송
                }catch(e : java.lang.Exception)
                {

                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                //사진 선택 취소
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.profileImage -> {
                    goToAlbum()
            }
            R.id.my_information_adjust_button ->{ // 수정 버튼 클릭 시

            }
        }
    }


}
