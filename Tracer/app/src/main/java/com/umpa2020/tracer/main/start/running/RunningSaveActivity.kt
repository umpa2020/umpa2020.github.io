package com.umpa2020.tracer.main.start.running


import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.Chart
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import com.umpa2020.tracer.util.gpx.GPXConverter
import kotlinx.android.synthetic.main.activity_running_save.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class RunningSaveActivity : AppCompatActivity(), OnMapReadyCallback {
  var switch = 0

  lateinit var infoData: InfoData
  lateinit var routeGPX: RouteGPX
  lateinit var traceMap: TraceMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(com.umpa2020.tracer.R.layout.activity_running_save)
    val smf = supportFragmentManager.findFragmentById(com.umpa2020.tracer.R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)

    infoData = intent.getParcelableExtra("InfoData")!!
    routeGPX = intent.getParcelableExtra("RouteGPX")!!
    val speedList = mutableListOf<Double>()
    val elevationList = mutableListOf<Double>()
    routeGPX.trkList.forEach {
      speedList.add(it.speed.get().toDouble())
      elevationList.add(it.elevation.get().toDouble())
    }
    distance_tv.text = String.format("%.2f", infoData.distance!! / 1000)
    val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
    formatter.timeZone = TimeZone.getTimeZone("UTC")

    time_tv.text = formatter.format(Date(infoData.time!!))
    speed_tv.text = String.format("%.2f", speedList.average())
    if (infoData.privacy == Privacy.PUBLIC) {
      racingRadio.isChecked = false
      racingRadio.isEnabled = false
      publicRadio.isChecked = true
    }
    val myChart = Chart(elevationList, speedList, chart)
    myChart.setChart()
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
            val callback = GoogleMap.SnapshotReadyCallback {
              try {
                val saveFolder = File(filesDir, "mapdata") // 저장 경로
                if (!saveFolder.exists()) {       //폴더 없으면 생성
                  saveFolder.mkdir()
                }
                val path = "racingMap" + saveFolder.list()!!.size + ".bmp"        //파일명 생성하는건데 수정필요
                //비트맵 크기에 맞게 잘라야함
                val myfile = File(saveFolder, path)                //로컬에 파일저장
                val out = FileOutputStream(myfile)
                it.compress(Bitmap.CompressFormat.PNG, 90, out)
                save(myfile.path)
              } catch (e: Exception) {
                Logg.d(e.toString())
              }
            }
            traceMap.captureMapScreen(callback)
            switch++
          }
        }
      }
    }
  }

  fun save(imgPath: String) {
    Logg.d( "Start Saving")
    // 타임스탬프 찍는 코드
    val timestamp = Date().time

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
    val gpxConverter = GPXConverter()
    val saveFolder = File(baseContext.filesDir, "routeGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val routeGpxFile = gpxConverter.classToGpx(routeGPX, saveFolder.path)
    // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
    val fstorage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
    Logg.d( "HI0?")
    val fRef = fstorage.reference.child("mapRoute").child(infoData.mapTitle!!)
    infoData.routeGPXPath = fRef.path

    Logg.d("HI1?")
    val fuploadTask = fRef.putFile(routeGpxFile)

    Logg.d("HI2?")
    fuploadTask.addOnFailureListener {
      Logg.d( "Success")
    }.addOnSuccessListener {
      Logg.d( "Fail : $it")
    }
    Logg.d( "HI3?")
    // db에 그려진 맵 저장하는 스레드 - 여기서는 실제 그려진 것 보다 후 보정을 통해서
    // 간략화 된 맵을 업로드 합니다.
    val db = FirebaseFirestore.getInstance()

    // InfoData class upload to database 참조 - 루트를 제외한 맵 정보 기술
    //TODO: routeGPX 파일로 스토리지에 업로드 후 경로 infoData에 넣어서 set
    db.collection("mapInfo").document(infoData.mapTitle!!).set(infoData)

    val rankingData = RankingData(UserInfo.nickname, UserInfo.nickname, infoData.time, 1)
    db.collection("rankingMap").document(infoData.mapTitle!!).set(rankingData)
    db.collection("rankingMap").document(infoData.mapTitle!!).collection("ranking")
      .document(rankingData.makerNickname + "||" + timestamp).set(rankingData)

    // db에 원하는 경로 및, 문서로 업로드

    // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
    val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
    val mapImageRef = storage.reference.child("mapImage").child(infoData.mapTitle!!)
    val uploadTask = mapImageRef.putFile(Uri.fromFile(File(imgPath)))
    uploadTask.addOnFailureListener {
      Logg.d( "스토리지 실패 = " + it.toString())
    }.addOnSuccessListener {
      finish()
    }

    // 위에 지정한 스레드 스타트
  }
  var track: MutableList<LatLng> = mutableListOf()
  fun loadRoute() {
    routeGPX.trkList.forEach {
      track.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
    }
  }
  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    traceMap.drawRoute(track,routeGPX.wptList)
  }
}