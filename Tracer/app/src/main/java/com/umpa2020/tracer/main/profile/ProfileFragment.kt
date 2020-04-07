package com.umpa2020.tracer.main.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.network.FBProfile
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
  lateinit var root: View
  var bundle = Bundle()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    Logg.i("onCreateView()")
    // Inflate the layout for this fragment

    val view = inflater.inflate(R.layout.fragment_profile, container, false)
    root = view

    // 공유 프리페런스에 있는 닉네임을 반영
    val profileNickname = view.findViewById<TextView>(R.id.profileIdTextView)
    profileNickname.text = UserInfo.nickname

    view.appSettingButton.setOnClickListener{
      val nextIntent = Intent(activity, AppSettingActivity::class.java)
      startActivity(nextIntent)
    }

    // 나의 활동 액티비티
//    val routeTextView = view.findViewById<TextView>(R.id.profileRouteTextView)
    view.profileRouteTextView.setOnClickListener {
      val nextIntent = Intent(activity, ProfileRouteActivity::class.java)
      startActivity(nextIntent)
    }

/*
        val recordTextView = view.findViewById<TextView>(R.id.profileRecordTextView)
        recordTextView.setOnClickListener {
            val nextIntent = Intent(activity, ProfileRecordActivity::class.java)
            startActivity(nextIntent)
        }
*/

    return view
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

  }

  override fun onStart() {
    Logg.i("onStart()")
    super.onStart()
  }

  override fun onResume() {
    Logg.i("onResume()")
    /**
     * 프로필 이미지랑 총 시간,거리 셋팅을 하는 함수
     */
    FBProfile().setProfile(root)
    super.onResume()
  }

  override fun onPause() {
    Logg.i("onPause()")
    super.onPause()
  }

  override fun onStop() {
    Logg.i("onStop()")
    super.onStop()
  }

  //  fun onClick(view: View) {
//    when (view.id) {
//      R.id.appSettingButton -> {
//
//      }
//      R.id.profileRouteTextView -> {
//        val nextIntent = Intent(activity, ProfileRouteActivity::class.java)
//        startActivity(nextIntent)
//      }
//    }
//  }
}