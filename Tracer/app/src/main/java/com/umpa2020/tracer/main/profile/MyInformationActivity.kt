package com.umpa2020.tracer.main.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.umpa2020.tracer.R
import com.umpa2020.tracer.network.FBProfile
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_my_information.*
import kotlinx.android.synthetic.main.activity_my_information.profileImage
import kotlinx.android.synthetic.main.activity_sign_up.*

class MyInformationActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_information)

    // 프로필 이미지 서버에서 가져와서 화면에 설정
    FBProfile().getProfileImage(profileImage, UserInfo.nickname)

    // TODO : 프로필 이미지 변경.
    // Shared에 저장된 유저 정보 설정정
    emailTextView.text = UserInfo.email
    nickNameTextView.text = UserInfo.nickname
    ageTextView.text = UserInfo.age
    genderTextView.text = UserInfo.gender
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.backImageBtn -> {
        finish()
      }
      R.id.profileImage -> {
        Logg.i("앨범으로")
        goToAlbum()
      }
      R.id.profileChangeButton -> {
        // TODO: 서버로 변경된 프로필 이미지 전송
        Logg.i("서버로 변경된 프로필 이미지 전송")

        FBProfile().changeProfileImage(bitmapImg!!)
        finish()
      }
    }
  }

  /**
   * 카메라 접근 권한 and 앨범 접근
   */
  private fun goToAlbum() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = MediaStore.Images.Media.CONTENT_TYPE
    startActivityForResult(intent, PICK_FROM_ALBUM)
  }

  var options: BitmapFactory.Options? = null
  private var bitmapImg: Bitmap? = null
  // intent 결과 받기
  override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
    super.onActivityResult(requestCode, resultCode, intentData)
    // 앨범
    if (requestCode == PICK_FROM_ALBUM) {
//
      if (resultCode == RESULT_OK) {
        try {
          val inputStream = intentData!!.data?.let { contentResolver.openInputStream(it) }

          // 프로필 사진을 비트맵으로 변환
          options = BitmapFactory.Options()
          options!!.inSampleSize = 2
          bitmapImg = BitmapFactory.decodeStream(inputStream, null, options)
          inputStream!!.close()

          profileImage.setImageBitmap(bitmapImg)
          profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: java.lang.Exception) {

        }
      } else if (resultCode == RESULT_CANCELED) {
        //사진 선택 취소
        Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
      }
    }
  }

  companion object {
    // 카메라 requestCode
    private val PICK_FROM_ALBUM = 1
  }
}
