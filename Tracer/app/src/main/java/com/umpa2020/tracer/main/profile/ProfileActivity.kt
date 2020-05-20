package com.umpa2020.tracer.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.main.profile.myActivity.ProfileActivityActivity
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.main.profile.settting.AppSettingActivity
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ID
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.appSettingButton
import kotlinx.android.synthetic.main.activity_profile.profileFragmentTotalDistance
import kotlinx.android.synthetic.main.activity_profile.profileFragmentTotalTime
import kotlinx.android.synthetic.main.activity_profile.profileIdTextView
import kotlinx.android.synthetic.main.activity_profile.profileImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity(), OnSingleClickListener {
  var userId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile)
    val progressBar = MyProgressBar()
    progressBar.show()

    userId = intent.extras?.getString(USER_ID).toString()
    if (userId != UserInfo.autoLoginKey) {
      profileRecordTextView.visibility = View.GONE
      appSettingButton.visibility = View.GONE
    }


    MainScope().launch {
      withContext(Dispatchers.IO) {
        FBProfileRepository().getProfile(userId)
      }.let {
        profileImageView.image(it.imgPath)
        profileFragmentTotalDistance.text = it.distance.prettyDistance
        profileFragmentTotalTime.text = it.time.format(m_s)
        progressBar.dismiss()
      }
      FBProfileRepository().getUserNickname(userId).let {
        profileIdTextView.text = it
      }
    }

    otherProfileRouteTextView.setOnClickListener(this)
    appSettingButton.setOnClickListener(this)
    profileRecordTextView.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.otherProfileRouteTextView -> {
        // 루트 클릭하면 해당 사용자가 만든 루트 볼 수 있는 페이지로 이동
        val nextIntent = Intent(this, ProfileRouteActivity::class.java)
        nextIntent.putExtra(USER_ID, userId)
        startActivity(nextIntent)
      }
      R.id.appSettingButton -> {  // 설정 버튼 누르면
        val nextIntent = Intent(this, AppSettingActivity::class.java)
        startActivity(nextIntent)
      }
      R.id.profileRecordTextView -> { // 나의 활동 액티비티
        val nextIntent = Intent(this, ProfileActivityActivity::class.java)
        startActivity(nextIntent)
      }
    }
  }
}
