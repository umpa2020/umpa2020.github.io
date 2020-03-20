package com.umpa2020.tracer.main.start.running


import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.dataClass.*
import com.umpa2020.tracer.trace.map.ViewerMap
import com.umpa2020.tracer.util.gpx.GPXConverter
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_running_save.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class RunningSaveActivity : AppCompatActivity() {
    var switch = 0

    lateinit var infoData: InfoData
    lateinit var routeGPX: RouteGPX
    lateinit var map: ViewerMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.umpa2020.tracer.R.layout.activity_running_save)

        infoData = intent.getParcelableExtra("Info Data")
        routeGPX = intent.getParcelableExtra("Route GPX")

        //TODO: 액티비티에 그리는 거 먼저
        val smf = supportFragmentManager.findFragmentById(com.umpa2020.tracer.R.id.map_viewer) as SupportMapFragment
        map = ViewerMap(smf, this, routeGPX)
        distance_tv.text = String.format("%.2f", infoData.distance!! / 1000)
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        time_tv.text = formatter.format(Date(infoData.time!!))
        //TODO:routeGPX 파싱해서 speed랑 고도 넣기
        //speed_tv.text = String.format("%.2f", infoData.speed.average())
        if (infoData.privacy == Privacy.PUBLIC) {
            racingRadio.isChecked = false
            racingRadio.isEnabled = false
            publicRadio.isChecked = true
        }
        //var chart = Chart(routeData.altitude, infoData.speed, chart)
        // chart.setChart()
    }

    fun onClick(view: View) {
        if (switch == 0) {
            when (view.id) {
                com.umpa2020.tracer.R.id.save_btn -> {
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
        Log.d("Save", "Start Saving")
        // 타임스탭프 찍는 코드
        val dt = Date()
        val full_sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())

        val date = full_sdf.parse(dt.toString())
        val timestamp = date!!.time


        // 인포데이터에 필요한 내용을 저장하고
        infoData.makersNickname = UserInfo.nickname
        infoData.mapImage = imgPath
        infoData.mapTitle = mapTitleEdit.text.toString() + "||" + timestamp.toString()
        infoData.mapExplanation = mapExplanationEdit.text.toString()
        infoData.makersNickname = UserInfo.nickname
        infoData.execute = 0
        infoData.likes = 0

        when (privacyRadioGroup.checkedRadioButtonId) {
            com.umpa2020.tracer.R.id.racingRadio -> infoData.privacy = Privacy.RACING
            com.umpa2020.tracer.R.id.publicRadio -> infoData.privacy = Privacy.PUBLIC
            com.umpa2020.tracer.R.id.privateRadio -> infoData.privacy = Privacy.PRIVATE
        }

        // 맵 타이틀과, 랭킹 중복 방지를 위해서 시간 값을 넣어서 중복 방지
        var gpxConverter = GPXConverter()
        var saveFolder = File(baseContext.filesDir, "routeGPX") // 저장 경로
        if (!saveFolder.exists()) {       //폴더 없으면 생성
            saveFolder.mkdir()
        }
        var routeGpxFile=gpxConverter.classToGpx(routeGPX, saveFolder.path)
        // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
        val fstorage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
        Log.d("Save", "HI0?")
        val fRef = fstorage.reference.child("mapRoute").child(infoData.mapTitle!!)

        Log.d("Save", "HI1?")
        var fuploadTask = fRef.putFile(routeGpxFile)

        Log.d("Save", "HI2?")
        fuploadTask.addOnFailureListener {
            Log.d("Save", "Success")
        }.addOnSuccessListener {
            Log.d("Save", "Fail : $it")
        }
        Log.d("Save", "HI3?")
        // db에 그려진 맵 저장하는 스레드 - 여기서는 실제 그려진 것 보다 후 보정을 통해서
        // 간략화 된 맵을 업로드 합니다.
        val db = FirebaseFirestore.getInstance()

        // InfoData class upload to database 참조 - 루트를 제외한 맵 정보 기술
        //TODO: routeGPX 파일로 스토리지에 업로드 후 경로 infoData에 넣어서 set
        db.collection("mapInfo").document(infoData.mapTitle!!).set(infoData)


        //TODO: 랭킹 부분 구현 필요 레이싱에도 같은 구조 필요
        val rankingData = RankingData(UserInfo.nickname, UserInfo.nickname, infoData.time)
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

        // 위에 지정한 스레드 스타트
    }
}
