package com.korea50k.tracer.start

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.GetMapData
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.RecordData
import com.korea50k.tracer.dataClass.RouteData
import kotlinx.android.synthetic.main.activity_running_save.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RunningSaveActivity : AppCompatActivity() {
    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET)
    //권한 체크
    private fun checkPermissions() {
        var rejectedPermissionList = ArrayList<String>()
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                        }
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_save)

        // 등록한 퍼미션 확인 과정
        checkPermissions()

        // 맵 타이틀과, 랭킹 중복 방지를 위해서 시간 값을 넣어서 중복 방지
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")

        val db = FirebaseFirestore.getInstance()
        val speed: List<Double> = listOf(1.0, 2.3, 3.3) // 순간 속력

        // InfoData class 참조 - 루트를 제외한 맵 정보 기술
        val infoData = InfoData(
            "jung_beengle", "mapTitle_test", "맵 설명",
            "스토리지에 있는 image 파일 경로", "10.32km", "12:22", 2, 0, "RANKING", speed
        )
        db.collection("maps").document("map1||"+ full_sdf.format(dt).toString()).set(infoData)


        val altitude: List<Double> = listOf(1.0, 2.3, 3.3) // 고도
        val latitude: List<Double> = listOf(1.0, 2.3, 3.3) // 위도
        val longitude: List<Double> = listOf(1.0, 2.3, 3.3) // 경도

        // RouteData class 참조 - 루트 정보만 표기 (위도, 경도, 고도)
        val routeData = RouteData(altitude, latitude, longitude)
        db.collection("maps").document("map1||"+ full_sdf.format(dt).toString()).collection("route").document("route")
            .set(routeData)


        val recordData = RecordData("jungbin", "13:33")
        db.collection("maps").document("map1||"+ full_sdf.format(dt).toString()).collection("record")
            .document("jungbin||"+ full_sdf.format(dt).toString()).set(recordData)
        // db에 원하는 경로 및, 문서로 업로드


        // db에 있는 모든 mapdata 받아오는 클래스
        var getMapData = GetMapData()
        getMapData.getMapData()

        // Reference to an image file in Cloud Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/dog.jpg")

        Log.d("ssmm11", "storage = "+ storageReference)
        // ImageView in your Activity
        val imageView = saveTestImageview

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)



        imageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Log.d("ssmm11", "흐음"+imageRef.downloadUrl)
                Glide.with(this@RunningSaveActivity)
                    .load(task.result)
                    .override(1024, 980)
                    .into(imageView)
            } else {
                Log.d("ssmm11", "실패")
            }
        }
    }
}
