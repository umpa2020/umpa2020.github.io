package com.umpa2020.tracer.main.start.running


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.customUI.WorkaroundMapFragment
import com.umpa2020.tracer.dataClass.*
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPointType.FINISH_POINT
import com.umpa2020.tracer.gpx.WayPointType.START_POINT
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ROUTE
import com.umpa2020.tracer.network.BaseFB.Companion.TRACK_COUNT_0
import com.umpa2020.tracer.network.BaseFB.Companion.TRACK_COUNT_49
import com.umpa2020.tracer.network.BaseFB.Companion.TRACK_COUNT_9
import com.umpa2020.tracer.network.FBAchievementRepository
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.util.*
import kotlinx.android.synthetic.main.activity_running_save.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class RunningSaveActivity : BaseActivity(), OnMapReadyCallback, OnSingleClickListener {
  lateinit var mapInfo: MapInfo
  lateinit var routeGPX: RouteGPX
  lateinit var traceMap: TraceMap
  val speedList = mutableListOf<Double>()
  lateinit var progressBar: MyProgressBar
  var routeGPXUri = "init"


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_running_save)
    progressBar = MyProgressBar()
    mapInfo = intent.getParcelableExtra("InfoData")!!
    routeGPXUri = intent.getStringExtra(RacingActivity.ROUTE_GPX)!!
    routeGPX = Uri.parse(routeGPXUri).gpxToClass()

    val wmf =
      supportFragmentManager.findFragmentById(R.id.map_viewer) as WorkaroundMapFragment
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
    distance_tv.text = mapInfo.distance.prettyDistance

    time_tv.text = mapInfo.time.format(m_s)
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
              launch {
                save(myfile.path)
              }
            } catch (e: Exception) {

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


  suspend fun save(imgPath: String) {
    val saveFolder = File(baseContext.filesDir, "routeGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    routeGPX.addDirectionSign()
    routeGPX.wptList.forEach {

    }
    val routeGpxFile = routeGPX.classToGpx(saveFolder.path)

    // 타임스탬프 찍는 코드
    val timestamp = Date().time

    // 인포데이터에 필요한 내용을 저장하고
    mapInfo.mapId = mapTitleEdit.text.toString() + timestamp.toString()
    mapInfo.makerId = UserInfo.autoLoginKey
    mapInfo.mapTitle = mapTitleEdit.text.toString()
    mapInfo.mapImagePath = "mapImage/${mapInfo.mapTitle}"
    mapInfo.mapExplanation = mapExplanationEdit.text.toString()
    mapInfo.plays = 1
    mapInfo.likes = 0
    mapInfo.maxSpeed = speedList.max()!!
    mapInfo.averageSpeed = speedList.average()
    mapInfo.routeGPXPath = "$MAP_ROUTE/${mapInfo.mapId}/${mapInfo.mapId}"
    mapInfo.createTime = timestamp

    val rankingData = RankingData(
      UserInfo.nickname,
      UserInfo.autoLoginKey,
      UserInfo.nickname,
      mapInfo.time,
      true,
      speedList.max(),
      speedList.average(),
      "${MAP_ROUTE}/${mapInfo.mapId}/racingGPX/${UserInfo.autoLoginKey}"
    )
    val activityData = ActivityData(mapInfo.mapId, timestamp, mapInfo.distance, mapInfo.time, BaseFB.ActivityMode.MAP_SAVE)
    val trophyData = TrophyData(mapInfo.mapId, 1)


    FBMapRepository().uploadMap(mapInfo, rankingData, activityData, timestamp.toString(), routeGpxFile, Uri.fromFile(File(imgPath)), trophyData)
    FBAchievementRepository().incrementPlays(mapInfo.makerId)


    // 업로드하면 로컬 파일을 삭제
    routeGPXUri.fileDelete()

    launch {
      val uid = UserInfo.autoLoginKey
      FBAchievementRepository().getAchievement(uid).let {
        when (it?.trackMake) {
          TRACK_COUNT_0 -> {
            FBAchievementRepository().setTrackMaker1Emblem(uid)
          }
          TRACK_COUNT_9 -> {
            FBAchievementRepository().setTrackMaker10Emblem(uid)
          }
          TRACK_COUNT_49 -> {
            FBAchievementRepository().setTrackMaker50Emblem(uid)
          }
          null -> {

          }
        }
        FBAchievementRepository().incrementTrackMake(uid)
      }
    }

    progressBar.dismiss()
    finish()
  }


  override fun onMapReady(googleMap: GoogleMap) {

    traceMap = TraceMap(googleMap) //구글맵
    traceMap.drawRoute(routeGPX.trkList.toList(), routeGPX.wptList.filter { it.type == START_POINT || it.type == FINISH_POINT })
  }
}
