package com.umpa2020.tracer.main.start.running


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.customUI.WorkaroundMapFragment
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.util.*
import kotlinx.android.synthetic.main.activity_running_save.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ROUTE
import com.umpa2020.tracer.network.FBMapRepository

class RunningSaveActivity : AppCompatActivity(), OnMapReadyCallback, OnSingleClickListener {
  var switch = 0

  lateinit var infoData: InfoData
  lateinit var routeGPX: RouteGPX
  lateinit var traceMap: TraceMap
  val speedList = mutableListOf<Double>()
  lateinit var progressBar: MyProgressBar


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(com.umpa2020.tracer.R.layout.activity_running_save)
    progressBar = MyProgressBar()
    infoData = intent.getParcelableExtra("InfoData")!!
    routeGPX = intent.getParcelableExtra("RouteGPX")!!
    val wmf =
      supportFragmentManager.findFragmentById(com.umpa2020.tracer.R.id.map_viewer) as WorkaroundMapFragment
    wmf.getMapAsync(this)
    wmf.setListener(object : WorkaroundMapFragment.OnTouchListener {
      override fun onTouch() {
        runningSaveScrollView.requestDisallowInterceptTouchEvent(true);
      }
    })

    val elevationList = mutableListOf<Double>()
    routeGPX.trkList.forEach {
      speedList.add(it.speed!!)
      elevationList.add(it.alt)
    }
    distance_tv.text = infoData.distance!!.prettyDistance

    time_tv.text = infoData.time!!.format(m_s)
    speed_tv.text = String.format("%.2f", speedList.average())

    val myChart = Chart(elevationList, speedList, chart)
    myChart.setChart()
    save_btn.setOnClickListener(this)
    runningSaveDeleteButton.setOnClickListener(this)
  }

  override fun onBackPressed() {
    if (::noticePopup.isInitialized && noticePopup.isShowing) {
      noticePopup.dismiss()
    } else {
      deleteSaving()
    }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.runningSaveDeleteButton -> {
        deleteSaving()
      }
      R.id.save_btn -> {
        if (mapTitleEdit.text.toString() == "") {
          mapTitleEdit.hint = "제목을 설정해주세요"
          mapTitleEdit.setHintTextColor(Color.RED)
        } else if (mapExplanationEdit.text.toString() == "") {
          mapExplanationEdit.hint = "맵 설명을 작성해주세요"
          mapExplanationEdit.setHintTextColor(Color.RED)
        } else {
          progressBar.show()
          val callback = GoogleMap.SnapshotReadyCallback {
            try {
              //save_btn.isEnabled = false
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
        }
      }
    }
  }

  lateinit var noticePopup: ChoicePopup
  private fun deleteSaving() {
    noticePopup = ChoicePopup(this, getString(R.string.select_type),
      getString(R.string.Are_you_sure_to_delete_this),
      getString(R.string.yes), getString(R.string.no),
      View.OnClickListener {
        //랭킹 기록용 버튼 눌렀을 때
        val intent = Intent(App.instance.context(), MainActivity::class.java)
        noticePopup.dismiss()
        startActivity(intent)
        finish()
      },
      View.OnClickListener {
        noticePopup.dismiss()
      })
    noticePopup.show()
  }


  fun save(imgPath: String) {
    val saveFolder = File(baseContext.filesDir, "routeGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    routeGPX.addDirectionSign()
    routeGPX.wptList.forEach {
      Logg.d("lat : ${it.lat} lon : ${it.lon} desc : ${it.desc} ")
    }
    val routeGpxFile = routeGPX.classToGpx(saveFolder.path)

    // 타임스탬프 찍는 코드
    val timestamp = Date().time

    // 인포데이터에 필요한 내용을 저장하고
    infoData.mapId = mapTitleEdit.text.toString() + timestamp.toString()
    infoData.makerId = UserInfo.autoLoginKey
    infoData.mapTitle = mapTitleEdit.text.toString()
    infoData.mapImagePath = "mapImage/${infoData.mapTitle}"
    infoData.mapExplanation = mapExplanationEdit.text.toString()
    infoData.plays = 1
    infoData.likes = 0
    infoData.maxSpeed = speedList.max()!!
    infoData.averageSpeed = speedList.average()
    infoData.routeGPXPath = "$MAP_ROUTE/${infoData.mapId}/${infoData.mapId}"

    val rankingData = RankingData(
      UserInfo.nickname,
      UserInfo.autoLoginKey,
      UserInfo.nickname,
      infoData.time,
      true,
      speedList.max().toString(),
      speedList.average().toString(),
      "${BaseFB.MAP_ROUTE}/${infoData.mapId}/racingGPX/${UserInfo.autoLoginKey}"
    )
    val activityData = ActivityData(infoData.mapId, timestamp, infoData.distance, "map save")
    Logg.d("Start Upload")

    FBMapRepository().uploadMap(infoData, rankingData, activityData, timestamp.toString(), routeGpxFile, Uri.fromFile(File(imgPath)))
    Logg.d("Finish Upload")
    progressBar.dismiss()
    finish()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.drawRoute(routeGPX.trkList.toList(), routeGPX.wptList.filter { it.type == START_POINT || it.type == FINISH_POINT })
  }
}