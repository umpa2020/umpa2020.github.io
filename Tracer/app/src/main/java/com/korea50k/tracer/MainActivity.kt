package com.korea50k.tracer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.MapData
import com.korea50k.tracer.dataClass.RecordData
import com.korea50k.tracer.dataClass.RouteData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = "ssmm11"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()
        val speed: List<Double> = listOf(1.0, 2.3, 3.3) // 순간 속력

        // InfoData class 참조 - 루트를 제외한 맵 정보 기술
        val infoData = InfoData(
            "jung_beengle", "mapTitle_test", "맵 설명",
            "스토리지에 있는 image 파일 경로", "10.32km", "12:22", 2, 0, "RANKING", speed
        )
        db.collection("maps").document("maptitle_1").collection("info").document("info")
            .set(infoData)


        val altitude: List<Double> = listOf(1.0, 2.3, 3.3) // 고도
        val latitude: List<Double> = listOf(1.0, 2.3, 3.3) // 위도
        val longitude: List<Double> = listOf(1.0, 2.3, 3.3) // 경도

        // RouteData class 참조 - 루트 정보만 표기 (위도, 경도, 고도)
        val routeData = RouteData(altitude, latitude, longitude)
        db.collection("maps").document("maptitle_1").collection("route").document("route")
            .set(routeData)

        val recordData = RecordData("sugine", "11:33")
        db.collection("maps").document("maptitle_1").collection("record")
            .document("challenger nickname").set(recordData)
        // db에 원하는 경로 및, 문서로 업로드


        // db에 있는 모든 mapdata 받아오는 클래스
        var getMapData = GetMapData()
        getMapData.getMapData()
    }

}
