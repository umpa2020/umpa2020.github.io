package com.korea50k.tracer.start

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R

import com.korea50k.tracer.util.UserInfo
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.Privacy
import com.korea50k.tracer.dataClass.RankingData
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.dataClass.*
import com.korea50k.tracer.map.ViewerMap
import com.korea50k.tracer.util.Chart
import kotlinx.android.synthetic.main.activity_running_save.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RunningSaveActivity : AppCompatActivity() {
    var switch = 0

    lateinit var infoData: InfoData
    lateinit var routeData: RouteData
    lateinit var mapSaveThread: Thread
    lateinit var map: ViewerMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_save)

        infoData = intent.getParcelableExtra("Info Data")
        routeData = intent.getParcelableExtra("Route Data")

        //TODO: 액티비티에 그리는 거 먼저
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = ViewerMap(smf, this, routeData)
        distance_tv.text = String.format("%.2f", infoData.distance!! / 1000)
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

        time_tv.text = formatter.format(Date(infoData.time!!))

        speed_tv.text = String.format("%.2f", infoData.speed.average())
        if (infoData.privacy == Privacy.PUBLIC) {
            racingRadio.isChecked = false
            racingRadio.isEnabled = false
            publicRadio.isChecked = true
        }
        var chart = Chart(routeData.altitude, infoData.speed, chart)
        chart.setChart()
    }

    fun onClick(view: View) {
        if (switch == 0) {
            when (view.id) {
                R.id.save_btn -> {
                    if (mapTitleEdit.text.toString() == "") {
                        mapTitleEdit.hint = "제목을 설정해주세요"
                        mapTitleEdit.setHintTextColor(Color.RED)
                    } else if (mapExplanationEdit.text.toString() == "") {
                        mapExplanationEdit.hint = "맵 설명을 작성해주세요"
                        mapExplanationEdit.setHintTextColor(Color.RED)
                    } else {
                        map.CaptureMapScreen()
                        switch++
                    }
                }
            }
        }
    }

    fun save(imgPath: String) {
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")

        infoData.makersNickname = UserInfo.nickname
        infoData.mapImage = imgPath
        infoData.mapTitle = mapTitleEdit.text.toString() + "||" + full_sdf.format(dt).toString()
        infoData.mapExplanation = mapExplanationEdit.text.toString()
        infoData.makersNickname = UserInfo.nickname
        infoData.execute = 0
        infoData.likes = 0

        Log.d("ssmm11", "세이브 눌림")
        when (privacyRadioGroup.checkedRadioButtonId) {
            R.id.racingRadio -> infoData.privacy = Privacy.RACING
            R.id.publicRadio -> infoData.privacy = Privacy.PUBLIC
            R.id.privateRadio -> infoData.privacy = Privacy.PRIVATE
        }

        // 맵 타이틀과, 랭킹 중복 방지를 위해서 시간 값을 넣어서 중복 방지


        // db에 그려진 맵 저장하는 스레드 - 여기서는 실제 그려진 것 보다 후 보정을 통해서
        // 간략화 된 맵을 업로드 합니다.
        mapSaveThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()

            // InfoData class upload to database 참조 - 루트를 제외한 맵 정보 기술
            db.collection("mapInfo").document(infoData.mapTitle!!).set(infoData)

            // RouteData class upload to database 참조 - 루트 정보만 표기 (위도경도, 고도, 마커의 위도경도)
            var routeDataOne = RouteDataOne(routeData.altitude, routeData.markerlatlngs)

            db.collection("mapRoute").document(infoData.mapTitle!!).set(routeDataOne)
            for (index in routeData.latlngs.indices) {
                var routeDataTwo = RouteDataTwo(index, routeData.latlngs[index])
                Log.d("ssmm11", "" + index + " = " + routeDataTwo)
                db.collection("mapRoute").document(infoData.mapTitle!!)
                    //.collection("routes").add(routeDataTwo)
                    .collection("routes").document(index.toString()).set(routeDataTwo)
            }

            //TODO: 랭킹 부분 구현 필요 레이싱에도 같은 구조 필요
            val rankingData = RankingData(UserInfo.nickname, UserInfo.nickname, infoData.time)
            //db.collection("rankingMap").document(infoData.mapTitle!!).set(rankingData)
            db.collection("rankingMap").document(infoData.mapTitle!!).set(rankingData)
            db.collection("rankingMap").document(infoData.mapTitle!!).collection("ranking")
                .document(rankingData.makerNickname + "||" + full_sdf.format(dt)).set(rankingData)

            // db에 원하는 경로 및, 문서로 업로드


            // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
            val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
            val mapImageRef = storage.reference.child("mapImage").child(infoData.mapTitle!!)
            var uploadTask = mapImageRef.putFile(Uri.fromFile(File(imgPath)))
            uploadTask.addOnFailureListener {
                Log.d("ssmm11", "스토리지 실패 = " + it.toString())

            }.addOnSuccessListener {
                finish()
            }
        })

        // 위에 지정한 스레드 스타트
        mapSaveThread.start()
    }
}
