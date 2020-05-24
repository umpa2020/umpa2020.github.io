package com.umpa2020.tracer.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.locationBackground.ServiceStatus
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.util.UserInfo
import com.umpa2020.tracer.viewModel.LocationViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
  private var doubleBackToExitPressedOnce1 = false

  companion object {

    lateinit var locationViewModel: LocationViewModel
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navController = findNavController(R.id.nav_host_fragment)
    bottom_navigation.setupWithNavController(navController)
    // 바텀 아이템 중복 선택 시 중복 작업 제거
    bottom_navigation.setOnNavigationItemReselectedListener{
      // do nothing
    }

    locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

    // 앱이 처음 다운되었으면 광운대학교로 위치 Shared에 설정.
    if (UserInfo.lat == 0.0f && UserInfo.lng == 0.0f) {

      UserInfo.lat = 37.619606f
      UserInfo.lng = 127.059798f
    } else {

    }


    TTS.speech(" ")
    startService() // 서비스 시작.

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

  }

  override fun onStop() {
    super.onStop()

  }

  override fun onPause() {
    super.onPause()
    Logg.d("onPause()")
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
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

    Intent(App.applicationContext(), LocationBackgroundService::class.java).also {
      it.action = action.name


      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상부터 foregroundService로 실행.
        startForegroundService(it)
      } else {
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
