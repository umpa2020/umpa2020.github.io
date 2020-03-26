package com.umpa2020.tracer.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.locationBackground.ServiceStatus
import com.umpa2020.tracer.main.profile.ProfileFragment
import com.umpa2020.tracer.main.ranking.RankingFragment
import com.umpa2020.tracer.main.start.StartFragment
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  //bottomNavigation 아이템 선택 리스너
  private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    var selectedFragment: Fragment? = null//선택된 프래그먼트 저장하는 변수

    when (item.itemId) { //선택된 메뉴에 따라서 선택된 프래그 먼트 설정
      R.id.navigation_start -> selectedFragment = StartFragment()
      R.id.navigation_profile -> selectedFragment = ProfileFragment()
      R.id.navigation_ranking -> selectedFragment = RankingFragment()
    }

    //동적으로 프래그먼트 교체
    supportFragmentManager.beginTransaction().replace(
      R.id.container,
      selectedFragment!!
    ).commit()

    true
  }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Logg.d("Hello I'm New")
    setContentView(R.layout.activity_main)

    startService()

    bottom_navigation.selectedItemId = R.id.navigation_start
    supportFragmentManager.beginTransaction().replace(
      R.id.container,
      StartFragment()
    ).commit()
    TTS.set(applicationContext)

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
    Logg.d("이건 실행되잖아?")
    Intent(applicationContext, LocationBackgroundService::class.java).also {
      it.action = action.name
      Logg.d(action.toString())
      startService(it)
    }
  }

  companion object {
    val TAG = "service"
    val WSY = "WSY"
    private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    const val MESSENGER_INTENT_KEY = "msg-intent-key"
  }
}