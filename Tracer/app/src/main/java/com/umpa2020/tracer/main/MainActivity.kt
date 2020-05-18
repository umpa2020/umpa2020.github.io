package com.umpa2020.tracer.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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
  }

  override fun onStart() {
    super.onStart()

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
