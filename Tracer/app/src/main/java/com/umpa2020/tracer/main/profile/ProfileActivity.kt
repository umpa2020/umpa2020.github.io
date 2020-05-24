package com.umpa2020.tracer.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.main.profile.myActivity.ProfileActivityActivity
import com.umpa2020.tracer.main.profile.myachievement.ProfileAchievementActivity
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.main.profile.settting.AppSettingActivity
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ID
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : BaseActivity(), OnSingleClickListener {
  var userId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile)
    val progressBar = MyProgressBar()
    progressBar.show()

    userId = intent.extras?.getString(USER_ID).toString()
    if (userId != UserInfo.autoLoginKey) {
      otherProfileRecordTextView.visibility = View.GONE
      otherProfileRecordMoreTextView.visibility = View.GONE
      appSettingButton.visibility = View.GONE
    }


    launch {
      withContext(Dispatchers.IO) {
        FBProfileRepository().getProfile(userId)
      }.let {
        profileImageView.image(it.imgPath)
        profileFragmentTotalDistance.text = it.distance.prettyDistance
        profileFragmentTotalTime.text = it.time.format(m_s)
      }
      FBProfileRepository().getUserNickname(userId).let {
        profileIdTextView.text = it
      }

      FBUsersRepository().listUserAchievement(userId).let {
        otherMedal1th?.text = it[0].toString()
        otherMedal2nd?.text = it[1].toString()
        otherMedal3rd?.text = it[2].toString()
        progressBar.dismiss()
      }
    }

    otherProfileRouteTextView.setOnClickListener(this)
    appSettingButton.setOnClickListener(this)
    otherProfileRecordTextView.setOnClickListener(this)
    otherProfileAchievementTextView.setOnClickListener(this)
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
      R.id.otherProfileRecordTextView -> { // 나의 활동 액티비티
        val nextIntent = Intent(this, ProfileActivityActivity::class.java)
        nextIntent.putExtra(USER_ID, userId)
        startActivity(nextIntent)
      }
      R.id.otherProfileAchievementTextView -> {
        val nextIntent = Intent(this, ProfileAchievementActivity::class.java)
        nextIntent.putExtra(USER_ID, userId)
        startActivity(nextIntent)
      }
    }
  }
}
