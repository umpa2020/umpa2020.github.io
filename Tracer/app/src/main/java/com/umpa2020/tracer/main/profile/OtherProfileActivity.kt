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
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_other_profile.*
import kotlinx.android.synthetic.main.activity_other_profile.profileFragmentTotalDistance
import kotlinx.android.synthetic.main.activity_other_profile.profileFragmentTotalTime
import kotlinx.android.synthetic.main.activity_other_profile.profileIdTextView
import kotlinx.android.synthetic.main.activity_other_profile.profileImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtherProfileActivity : AppCompatActivity(), OnSingleClickListener {
  var userId = ""
  val progressBar = MyProgressBar()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_other_profile)
    progressBar.show()

    val intent = intent
    //전달 받은 값으로 Title 설정
    userId = intent.extras?.getString("uid").toString()

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
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.otherProfileRouteTextView -> {
        // 루트 클릭하면 해당 사용자가 만든 루트 볼 수 잇는 페이지로 이동
        val nextIntent = Intent(this, ProfileRouteActivity::class.java)
        nextIntent.putExtra("UID", userId)
        startActivity(nextIntent)
      }
    }
  }
}
