package com.superdroid.facemaker.Activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.otto.Subscribe
import com.superdroid.facemaker.EventBus.Events
import com.superdroid.facemaker.EventBus.GlobalBus
import com.superdroid.facemaker.R
import com.superdroid.facemaker.fragment.LoadFragment
import com.superdroid.facemaker.fragment.MainFragment

class MainActivity:AppCompatActivity() {

    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    private val fragmentManager = supportFragmentManager
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    // 4개의 메뉴에 들어갈 Fragment들
     val mainFragment = MainFragment()
    val loadFragment= LoadFragment()
    private val menuTextView: AppCompatButton? = null

    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    lateinit var storage: FirebaseStorage
    lateinit var mStorageReference: StorageReference
    var LoadFileName=""
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()          //모든 권한 확인
        storage = FirebaseStorage.getInstance()      //firebase 가져오기
        mStorageReference = storage.reference
        // 첫 화면 지정
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_layout, mainFragment).commitAllowingStateLoss()
        GlobalBus.getBus()?.register(this)
        // 버튼 클릭시 사용되는 리스너를 구현합니다.

        val bottomNavigationView =
            findViewById(R.id.bottomNavigationView_main_menu) as BottomNavigationView
       bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val transaction = fragmentManager.beginTransaction()

            // 어떤 메뉴 아이템이 터치되었는지 확인합니다.
            when (item.itemId) {
                R.id.menuitem_bottombar_main -> {
                    transaction.replace(R.id.fragment_layout, mainFragment).commitAllowingStateLoss()
                }
                R.id.menuitem_bottombar_load -> {
                    transaction.replace(R.id.fragment_layout, loadFragment).commitAllowingStateLoss()

                }
            }
            true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalBus.getBus()?.unregister(this)
    }

    @Subscribe
    fun Receive_LoadToMain(e: Events.LoadToMain){
        print_log(e.fileName)
        LoadFileName=e.fileName
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_layout, mainFragment).commitAllowingStateLoss()
    }
    @Subscribe
    fun Receive_FileListRequest(e: Events.FileListRequest){
        print_log("Receive_FileListRequest")
        var filelist=ArrayList<String>()
        var mapsRef = mStorageReference.child(("maps"))
        var a = mapsRef.listAll()
            .addOnSuccessListener {
                for (item in it.items) {
                    filelist.add(item.name)
                }
                print_log("Succes to get Filelist")
                GlobalBus.getBus()
                    ?.post(Events.FileListBack(filelist))
            }.addOnFailureListener {
                print_log("Fail to load")
            }
    }
    @Subscribe
    fun Receive_FileNameRequest(e: Events.FileNameRequest){
        GlobalBus.getBus()
            ?.post(Events.FileNameBack(LoadFileName))
    }
    fun print_log(text:String){
        Log.d(TAG,text.toString())
    }

}