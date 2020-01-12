package com.korea50k.RunShare.Activities.MainFragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_main.*
import androidx.drawerlayout.widget.DrawerLayout
import android.view.View
import android.widget.Toast
import com.korea50k.RunShare.Activities.Profile.MyInformationActivity
import com.korea50k.RunShare.Activities.Profile.SettingActivity
import com.korea50k.RunShare.Activities.Profile.UserActivity
import com.korea50k.RunShare.Activities.RankFragment.RankRecyclerClickActivity
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.Util.TTS


class MainActivity : AppCompatActivity() {
    lateinit var mViewPager:ViewPager
    lateinit var mMainPageAdapter: MainPageAdapter
    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.korea50k.RunShare.R.layout.activity_main)
        Log.d("WSY","Shared 저장 이메일 : " + SharedPreValue.getEMAILData(this))
        Log.d("WSY","Shared 저장 비번 : " + SharedPreValue.getPWDData(this))
        Log.d("WSY","Shared 저장 닉네임 : " + SharedPreValue.getNicknameData(this))
        Log.d("WSY","Shared 저장 나이 : " + SharedPreValue.getAgeData(this))
        Log.d("WSY","Shared 저장 성별 : " + SharedPreValue.getGenderData(this))
        checkPermissions()          //모든 권한 확인
        TTS.set(applicationContext)
        val mTabLayout = tabDots
        mTabLayout.addTab(mTabLayout.newTab())
        mTabLayout.addTab(mTabLayout.newTab())
        //mTabLayout.addTab(mTabLayout.newTab())

        mViewPager =pager
        mMainPageAdapter =
            MainPageAdapter(
                supportFragmentManager,
                mTabLayout.tabCount
            )
        mViewPager.adapter=mMainPageAdapter
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mViewPager.currentItem=1
        //DrawerLayout.Lock
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //drawerLayout.closeDrawer(drawer)
    }
    fun onClick(v:View){
        when(v.id){
            R.id.openDrawerButton->{
                drawerLayout.openDrawer(drawer)
            }
            R.id.closeDrawerButton->{
                drawerLayout.closeDrawer(drawer)
            }

            R.id.slide_profileLayout->{
                var intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
            R.id.slide_mydata_Button->{
                var intent = Intent(this, MyInformationActivity::class.java)
                startActivity(intent)
            }
            R.id.slide_setting_Button->{
                var intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
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
