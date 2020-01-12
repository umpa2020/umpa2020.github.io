package com.korea50k.RunShare.Activities.MainFragment

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
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
import androidx.appcompat.app.AlertDialog
import com.korea50k.RunShare.Activities.Profile.MyInformationActivity
import com.korea50k.RunShare.Activities.Profile.SettingActivity
import com.korea50k.RunShare.Activities.Profile.UserActivity
import com.korea50k.RunShare.Activities.RankFragment.RankRecyclerClickActivity
import com.korea50k.RunShare.Splash.SplashActivity
import com.korea50k.RunShare.Util.S3
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.Util.TTS
import kotlinx.android.synthetic.main.activity_my_information.*
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {
    val WSY = "WSY"

    lateinit var mViewPager: ViewPager
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
        Log.d("WSY", "Shared 저장 이메일 : " + SharedPreValue.getEMAILData(this))
        Log.d("WSY", "Shared 저장 비번 : " + SharedPreValue.getPWDData(this))
        Log.d("WSY", "Shared 저장 닉네임 : " + SharedPreValue.getNicknameData(this))
        Log.d("WSY", "Shared 저장 나이 : " + SharedPreValue.getAgeData(this))
        Log.d("WSY", "Shared 저장 성별 : " + SharedPreValue.getGenderData(this))
        checkPermissions()          //모든 권한 확인
        TTS.set(applicationContext)
        val mTabLayout = tabDots
        mTabLayout.addTab(mTabLayout.newTab())
        mTabLayout.addTab(mTabLayout.newTab())
        //mTabLayout.addTab(mTabLayout.newTab())

        mViewPager = pager
        mMainPageAdapter =
            MainPageAdapter(
                supportFragmentManager,
                mTabLayout.tabCount
            )
        mViewPager.adapter = mMainPageAdapter
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mViewPager.currentItem = 1
        //DrawerLayout.Lock
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //drawerLayout.closeDrawer(drawer)

        slideProfileIdTextView.text = SharedPreValue.getNicknameData(this)

    }
    var imageUri : String? = null


    override fun onResume() {
        super.onResume()
        Log.d(WSY, "onResume()")
//        Thread(Runnable {
//            slideProfileImageView.setImageBitmap(
//                S3.downloadBitmap(
//                    SharedPreValue.getProfileData(
//                        this
//                    )!!
//                )
//            )
//        }).start()
        imageUri = SharedPreValue.getProfileData(this)
        class SetImageTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }
            var bm: Bitmap? = null

            override fun doInBackground(vararg params: Void?): String? {
                try {

                    Log.d(WSY, "Uri : " + imageUri)
                    bm = S3.downloadBitmap(imageUri!!)
                    Log.d(WSY, "비트맵 : " + bm.toString() )


                } catch (e : java.lang.Exception) {
                    Log.d(WSY, "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
                slideProfileImageView.setImageBitmap(bm)
            }
        }
        var Start = SetImageTask()
        Start.execute()
    }

    override fun onPause() {
        super.onPause()
        Log.d(WSY, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(WSY, "onStop()")
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.openDrawerButton -> {
                drawerLayout.openDrawer(drawer)
            }
            R.id.closeDrawerButton -> {
                drawerLayout.closeDrawer(drawer)
            }

            R.id.slide_profileLayout -> {
                var nextIntent = Intent(this, UserActivity::class.java)
                nextIntent.putExtra("ID", SharedPreValue.getNicknameData(this))
                startActivity(nextIntent)
            }
            R.id.slide_mydata_Button -> {
                var nextIntent = Intent(this, MyInformationActivity::class.java)
                startActivity(nextIntent)
            }
            /*
            R.id.slide_setting_Button->{
                var nextIntent = Intent(this, SettingActivity::class.java)
                startActivity(nextIntent)
            }

             */
            R.id.slide_logout_Button -> {
                var builder = AlertDialog.Builder(this)
                builder.setTitle("안내").setMessage("로그아웃 하시겠습니까?")

                builder.setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        SharedPreValue.AllDataRemove(applicationContext)
                        restartApp()
                    }
                })
                builder.setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                })

                Log.d("WSY", "Shared 저장 이메일 : " + SharedPreValue.getEMAILData(this))
                Log.d("WSY", "Shared 저장 비번 : " + SharedPreValue.getPWDData(this))
                Log.d("WSY", "Shared 저장 닉네임 : " + SharedPreValue.getNicknameData(this))
                Log.d("WSY", "Shared 저장 나이 : " + SharedPreValue.getAgeData(this))
                Log.d("WSY", "Shared 저장 성별 : " + SharedPreValue.getGenderData(this))
                var alertDialog = builder.create()
                alertDialog.show()
            }
        }
    }

    // 로그아웃 시 앱 재실행 함수
    fun restartApp() {
        finishAffinity()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        System.exit(0)
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
