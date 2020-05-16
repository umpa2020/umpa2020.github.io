package com.umpa2020.tracer.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.locationBackground.ServiceStatus
import com.umpa2020.tracer.main.challenge.ChallengeFragment
import com.umpa2020.tracer.main.profile.ProfileFragment
import com.umpa2020.tracer.main.ranking.RankingFragment
import com.umpa2020.tracer.main.start.StartFragment
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.util.UserInfo
import com.umpa2020.tracer.viewModel.LocationViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
  private var doubleBackToExitPressedOnce1 = false

  var selectedFragment: Fragment? = null//선택된 프래그먼트 저장하는 변수

  //bottomNavigation 아이템 선택 리스너
  private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    val currentClickTime = SystemClock.uptimeMillis()
    val elapsedTime = currentClickTime - Constants.mLastClickTime
    Constants.mLastClickTime = currentClickTime

    // 중복클릭 아닌 경우
    if (elapsedTime > Constants.MIN_CLICK_INTERVAL) {
      when (item.itemId) { //선택된 메뉴에 따라서 선택된 프래그 먼트 설정
        R.id.navigation_start -> selectedFragment = StartFragment()
        R.id.navigation_profile -> selectedFragment = ProfileFragment()
        R.id.navigation_ranking -> selectedFragment = RankingFragment()
        R.id.navigation_challenge -> selectedFragment = ChallengeFragment()
      }

      Logg.i("프래그먼트 개수 : " + supportFragmentManager.backStackEntryCount.toString())
      //동적으로 프래그먼트 교체
      supportFragmentManager.beginTransaction().replace(
        R.id.container,
        selectedFragment!!
      ).commit()

    }
    true
  }

  companion object {

    lateinit var locationViewModel: LocationViewModel
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

    // 앱이 처음 다운되었으면 광운대학교로 위치 Shared에 설정.
    if (UserInfo.lat == 0.0f && UserInfo.lng == 0.0f) {
      Logg.d("값 없음")
      UserInfo.lat = 37.619606f
      UserInfo.lng = 127.059798f
    }else{
      Logg.d("값 있음 : ${UserInfo.lat}, ${UserInfo.lng}")
    }


    TTS.speech(" ")
    startService() // 서비스 시작.

    bottom_navigation.selectedItemId = R.id.navigation_start
    supportFragmentManager.beginTransaction().replace(
      R.id.container,
      StartFragment()
    ).commit()
    selectedFragment = StartFragment()

    //선택한 메뉴로 프래그먼트 바꿈
    bottom_navigation.setOnNavigationItemSelectedListener(navListener)

    //회전됐을 때 프래그먼트 유지
    //처음 실행 했을때 초기 프래그먼트 설정
    if (savedInstanceState == null && UserInfo.permission == 1) {
      bottom_navigation.selectedItemId = R.id.navigation_start
      supportFragmentManager.beginTransaction().replace(
        R.id.container,
        StartFragment()
      ).commit()
    }
    Logg.d("restart service")

    // FCM 테스트는 해당 함수 실행 후 서버에 저장된 토큰 값으로 Cloud Message 실행.
    registerPushToken()
  }

  /**
   * 토큰 값을 서버에 저장하는 함수지만 토큰 값 없이 Cloud Message만 써도 전송 가능.
   *  다음과 같은 경우 토큰 재 발급.
   *  - 앱에서 인스턴스 ID 삭제
   *  - 새 기기에서 앱 복원
   *  - 사용자가 앱 삭제/재설치
   *  - 사용자가 앱 데이터 소거
   */
  private fun registerPushToken() {
    //v17.0.0 이전까지는
    ////var pushToken = FirebaseInstanceId.getInstance().token
    //v17.0.1 이후부터는 onTokenRefresh()-depriciated
    var pushToken: String? = null
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    val map = mutableMapOf<String, Any>()
    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
      pushToken = instanceIdResult.token
      Logg.d(pushToken.toString())
      map["pushtoken"] = pushToken!!
      FirebaseFirestore.getInstance().collection("pushtokens").document(uid).set(map)
    }
  }
//  override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//    if (selectedFragment != null) {
//      supportFragmentManager.putFragment(outState, KEY_FRAGMENT, currentFragment);
//    }
//    outState.putString(KEY_LIST_NAME, currentListName);
//    super.onSaveInstanceState(outState, outPersistentState)
//
//  }

  override fun onStart() {
    super.onStart()
    Logg.d(selectedFragment.toString())
    Logg.d(selectedFragment!!.id.toString())

    if (selectedFragment!!.id == Constants.PROFILE_FRAGMENT_ID) { //  ProfileFragment id = 2131296382
      Logg.d(selectedFragment!!.id.toString())
      Logg.i("누구냐!")
      supportFragmentManager.beginTransaction().detach(selectedFragment!!).attach(selectedFragment!!).commit()
    }
  }

  override fun onStop() {
    super.onStop()

  }

  override fun onDestroy() {
    super.onDestroy()
    stopService()
  }

  // as google doc says
  // Handler for incoming messages from the service.
  // private var mHandler: IncomingMessageHandler? = null

  /**
   * start service
   * main 액티비티 실행 시 위치 서비스 시작.
   */
  private fun startService() {
    startStopServiceCommand(ServiceStatus.START)
  }

  /**
   * stop service
   */
  private fun stopService() {
    startStopServiceCommand(ServiceStatus.STOP)
  }


  /**
   *  단순 서비스 시작 메소드
   *  action => ServiceStatus.START or ServiceStatus.STOP
   */

  private fun startStopServiceCommand(action: ServiceStatus) {
    Logg.i("startStopServiceCommand")
    Intent(App.applicationContext(), LocationBackgroundService::class.java).also {
      it.action = action.name
      Logg.d(action.toString())

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상부터 foregroundService로 실행.
        startForegroundService(it)
      }else {
        startService(it)
      }
    }
  }

  override fun onBackPressed() {
    if (doubleBackToExitPressedOnce1) {
      super.onBackPressed()
      return
    }
    this.doubleBackToExitPressedOnce1 = true
    Handler().postDelayed({ doubleBackToExitPressedOnce1 = false }, 3000)
  }
}
