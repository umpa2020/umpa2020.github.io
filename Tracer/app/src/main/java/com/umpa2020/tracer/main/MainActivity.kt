package com.umpa2020.tracer.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.locationBackground.ServiceStatus
import com.umpa2020.tracer.main.profile.ProfileFragment
import com.umpa2020.tracer.main.ranking.RankingFragment
import com.umpa2020.tracer.main.trace.StartFragment
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

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

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()          //모든 권한 확인
        TTS.set(applicationContext)

        // mHandler = IncomingMessageHandler()

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
    }

    private fun checkPermissions() {
        var rejectedPermissionList = ArrayList<String>()
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                multiplePermissionsCode
            )
        }
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<String>, grantResults: IntArray ) {
        when (requestCode)
        {
            multiplePermissionsCode -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                        }
                    }
                }
            }
        }
        if (UserInfo.permission == 0) {
            bottom_navigation.selectedItemId = R.id.navigation_start
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                StartFragment()
            ).commit()
        }
        UserInfo.permission = 1
        startService()
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
        Log.d(TAG, "이건 실행되잖아?")
        Intent(this, LocationBackgroundService::class.java).also {
            it.action = action.name
            Log.d(TAG, action.toString()) // 처음 실행 시 START
//            if (action == ServiceStatus.START) {
//                Log.d(TAG,"zzzzz")
//                val messengerIncoming = Messenger(mHandler)
//                it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
//            }

//            //  출처: https://mixup.tistory.com/59 [투믹스 작업장]
            // https://developer.android.com/about/versions/oreo/background?hl=ko
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(it)  // Oreo(26) 부터 지원
//                return
//            }
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