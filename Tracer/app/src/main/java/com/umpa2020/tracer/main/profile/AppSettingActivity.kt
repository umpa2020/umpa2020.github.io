package com.umpa2020.tracer.main.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.system.exitProcess


class AppSettingActivity : AppCompatActivity() {
  // firebase Auth
  private var mAuth: FirebaseAuth? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_app_setting)

    mAuth = FirebaseAuth.getInstance() // FirebaseAuth를 사용하기 위해서 인스턴스를 꼭 받아오기
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.informationEditButton -> {
        val intent = Intent(this, MyInformationActivity::class.java)
        startActivity(intent)
      }
      R.id.logoutButton -> {
        logOut()
      }
    }
  }


  var noticePopup: ChoicePopup? = null

  // TODO : 로그아웃하고 새로 로그인할 때 구글 계정 선택하는 팝업을 띄우고 싶은데 해결 못함. LoginActivity에서 건들여야 할듯
  private fun logOut() {
    // Shared의 정보 삭제
    noticePopup = ChoicePopup(this, "선택해주세요.", "로그아웃하시겠습니까?", "예", "아니오",
      View.OnClickListener {
        // 예
        UserInfo.autoLoginKey = " "
        UserInfo.email = " "
        UserInfo.nickname = " "
        UserInfo.age = " "
        UserInfo.gender = " "
        //TODO : 이건 정빈이가 추가한거 같은데 삭제 하니깐 약간 어플이 꼬이는거 같아서 물어보고 삭제하든가 하기
//        UserInfo.permission = 0
//        UserInfo.rankingLatLng = LatLng(0.0, 0.0)

        // 어플 재 시작
        ActivityCompat.finishAffinity(this)
        val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

      },
      View.OnClickListener {
        // 아니오
        noticePopup!!.dismiss()
      }
    )
    noticePopup!!.show()
  }
}
