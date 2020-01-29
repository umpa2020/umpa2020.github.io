package com.korea50k.tracer.Start

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.util.GetMapData
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.RankingData
import com.korea50k.tracer.dataClass.RouteData
import kotlinx.android.synthetic.main.activity_running_save.*
import java.text.SimpleDateFormat
import java.util.*

class RunningSaveActivity : AppCompatActivity() {
    var switch = 0

    lateinit var infoData : InfoData
    lateinit var routeData: RouteData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_save)

        infoData = intent.getParcelableExtra("Info Data")
/*
        //TODO:맵 그리는 거 조금 나중에
        *//*val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = ViewerMap(smf, this, runningData)*//*

        // 맵 타이틀과, 랭킹 중복 방지를 위해서 시간 값을 넣어서 중복 방지
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")

        val db = FirebaseFirestore.getInstance()

        // InfoData class 참조 - 루트를 제외한 맵 정보 기술
        val infoData = InfoData(
            "jung_beengle", "mapTitle_test", "맵 설명",
            "스토리지에 있는 image 파일 경로", "10.32km", "10356", 2, 0, "RANKING", speed
        )
        db.collection("maps").document("map1||"+ full_sdf.format(dt).toString()).set(infoData)


        val altitude: List<Double> = listOf(1.0, 2.3, 3.3) // 고도
        val latitude: List<Double> = listOf(1.0, 2.3, 3.3) // 위도
        val longitude: List<Double> = listOf(1.0, 2.3, 3.3) // 경도

        // RouteData class 참조 - 루트 정보만 표기 (위도, 경도, 고도)
        val routeData = RouteData(altitude, latitude, longitude)
        db.collection("maps").document("map1||"+ full_sdf.format(dt).toString()).collection("route").document("route")
            .set(routeData)


        val recordData = RankingData("jungbin", "13:33")
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




        //TODO:ImageView 에 이미지 박는 코드 (firebase)
        *//*
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
         *//*
    }

    fun onClick(view: View) {
        if (switch == 0) {
            when (view.id) {
                R.id.save_btn -> {
                    if(mapTitleEdit.text.toString()==""){
                        mapTitleEdit.hint="제목을 설정해주세요"
                        mapTitleEdit.setHintTextColor(Color.RED)
                    }else if(mapExplanationEdit.text.toString()==""){
                        mapExplanationEdit.hint="맵 설명을 작성해주세요"
                        mapExplanationEdit.setHintTextColor(Color.RED)
                    }
                    else{
                        //map.CaptureMapScreen()
                        switch++
                    }
                }
            }
        }*/
    }
}
