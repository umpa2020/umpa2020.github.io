package com.umpa2020.tracer.main.profile


import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.main.profile.myActivity.ProfileActivityActivity
import com.umpa2020.tracer.main.profile.myachievement.ProfileAchievementActivity
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.main.profile.settting.AppSettingActivity
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment() : Fragment(), OnSingleClickListener, Parcelable, CoroutineScope by MainScope() {
  lateinit var root: View
  var bundle = Bundle()
  val progressBar = MyProgressBar()
  var distance = 0.0

  constructor(parcel: Parcel) : this() {
    bundle = parcel.readBundle(Bundle::class.java.classLoader)!!
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    root = inflater.inflate(R.layout.fragment_profile, container, false)
    root.profileIdTextView.text = UserInfo.nickname
    root.appSettingButton.setOnClickListener(this)
    root.profileRouteTextView.setOnClickListener(this)
    root.profileRecordTextView.setOnClickListener(this)
    root.profileAchivementTextView.setOnClickListener(this)
    return root
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.appSettingButton -> {  // 설정 버튼 누르면
        val nextIntent = Intent(activity, AppSettingActivity::class.java)
        startActivity(nextIntent)
      }

      R.id.profileRouteTextView -> { // 나의 루트 액티비티
        val nextIntent = Intent(activity, ProfileRouteActivity::class.java)
        nextIntent.putExtra(BaseFB.USER_ID, UserInfo.autoLoginKey)
        startActivity(nextIntent)
      }

      R.id.profileRecordTextView -> { // 나의 활동 액티비티
        val nextIntent = Intent(activity, ProfileActivityActivity::class.java)
        startActivity(nextIntent)
      }

      R.id.profileAchivementTextView -> { // 나의 업적 액티비티
        val nextIntent = Intent(activity, ProfileAchievementActivity::class.java)
        nextIntent.putExtra(BaseFB.USER_ID, UserInfo.autoLoginKey)
        startActivity(nextIntent)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    progressBar.show()
    /**
     * 프로필 이미지랑 총 시간,거리 셋팅을 하는 함수
     * 프로필 변경을 하고 나오는 경우에도 적용된
     * 사진을 바로 보기 위해 Resume에서 적용
     */
    launch {
      withContext(Dispatchers.IO) {
        FBProfileRepository().getProfile(UserInfo.autoLoginKey)
      }.let {
        profileImageView.image(it.imgPath)
        profileFragmentTotalDistance.text = it.distance.prettyDistance
        distance = it.distance
        profileFragmentTotalTime.text = it.time.format(m_s)
      }
        FBUsersRepository().listUserAchievement(UserInfo.autoLoginKey).let {
          medal1th.text = it[0].toString()
          medal2nd.text = it[1].toString()
          medal3rd.text = it[2].toString()
          progressBar.dismiss()
        }
    }
  }

  override fun onPause() {
    super.onPause()
    progressBar.dismiss()
    // 갑자기 뒤로가면 코루틴 취소
    MainScope().cancel()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeBundle(bundle)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ProfileFragment> {
    override fun createFromParcel(parcel: Parcel): ProfileFragment {
      return ProfileFragment(parcel)
    }

    override fun newArray(size: Int): Array<ProfileFragment?> {
      return arrayOfNulls(size)
    }
  }
}