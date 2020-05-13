package com.umpa2020.tracer.main.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.main.profile.myrecord.ProfileRecordActivity
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.main.profile.settting.AppSettingActivity
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), OnSingleClickListener {
  lateinit var root: View
  var bundle = Bundle()
  val progressBar = MyProgressBar()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    Logg.i("onCreateView()")

    val view = inflater.inflate(R.layout.fragment_profile, container, false)
    root = view

    // 공유 프리페런스에 있는 닉네임을 반영
    view.profileIdTextView.text = UserInfo.nickname

    // 설정 버튼 누르면
    view.appSettingButton.setOnClickListener(this)

    // 나의 루트 액티비티
    view.profileRouteTextView.setOnClickListener(this)

    // 나의 활동 액티비티
    view.profileRecordTextView.setOnClickListener(this)

    return view
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.appSettingButton -> {  // 설정 버튼 누르면
        val nextIntent = Intent(activity, AppSettingActivity::class.java)
        startActivity(nextIntent)
      }

      R.id.profileRouteTextView -> { // 나의 루트 액티비티
        val nextIntent = Intent(activity, ProfileRouteActivity::class.java)
        nextIntent.putExtra("UID", UserInfo.autoLoginKey)
        startActivity(nextIntent)
      }

      R.id.profileRecordTextView -> { // 나의 활동 액티비티
        val nextIntent = Intent(activity, ProfileRecordActivity::class.java)
        //nextIntent.putExtra("nickname", UserInfo.nickname)
        startActivity(nextIntent)
      }
    }
  }

  override fun onResume() {
    super.onResume()

    Logg.i("onResume()")

    progressBar.show()

    /**
     * 프로필 이미지랑 총 시간,거리 셋팅을 하는 함수
     * 프로필 변경을 하고 나오는 경우에도 적용된
     * 사진을 바로 보기 위해 Resume에서 적용
     */
    MainScope().launch {
      withContext(Dispatchers.IO) {
        FBProfileRepository().getProfile(UserInfo.autoLoginKey)
      }.let {
        profileImageView.image(it.imgPath)
        profileFragmentTotalDistance.text = it.distance.prettyDistance
        profileFragmentTotalTime.text = it.time.format(m_s)
        progressBar.dismiss()
      }
    }
  }
}