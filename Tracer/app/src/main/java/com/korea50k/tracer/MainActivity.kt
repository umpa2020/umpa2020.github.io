package com.korea50k.tracer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.korea50k.tracer.ranking.RankingFragment
import com.korea50k.tracer.start.StartFragment
import kotlinx.android.synthetic.main.activity_main.*
import com.korea50k.tracer.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()          //모든 권한 확인


        //선택한 메뉴로 프래그먼트 바꿈
        bottom_navigation.setOnNavigationItemSelectedListener(navListener)
        //회전됐을 때 프래그먼트 유지
        //처음 실행 했을때 초기 프래그먼트 설정
        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId = R.id.navigation_start
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                StartFragment()
            ).commit()
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            /*
            R.id.mainTest -> {
                    val newIntent = Intent(this, RunningSaveActivity::class.java)
                    startActivity(newIntent)

            }

             */
        }

    }

    private fun checkPermissions() {
        var rejectedPermissionList = ArrayList<String>()
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
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
    }
}
