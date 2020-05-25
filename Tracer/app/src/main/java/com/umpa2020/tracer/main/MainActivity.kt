package com.umpa2020.tracer.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.locationBackground.ServiceStatus
import com.umpa2020.tracer.lockscreen.viewModel.LocationViewModel
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
  companion object {
    lateinit var locationViewModel: LocationViewModel
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navController = findNavController(R.id.nav_host_fragment)
    bottom_navigation.setupWithNavController(navController)
    // 바텀 아이템 중복 선택 시 중복 작업 제거
    bottom_navigation.setOnNavigationItemReselectedListener {
      // do nothing
    }

    locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

    // 앱이 처음 다운되었으면 광운대학교로 위치 Shared에 설정.
    if (UserInfo.lat == 0.0f && UserInfo.lng == 0.0f) {

      UserInfo.lat = 37.619606f
      UserInfo.lng = 127.059798f
    }

    //TTS 한번 실행 해야 뒤에 동작이 정상적으로 됨
    TTS.speech(" ")
    startService() // 서비스 시작.

  }

  override fun onDestroy() {
    super.onDestroy()

    stopService()
  }

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
}
