package com.umpa2020.tracer.main.start.running

import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.umpa2020.tracer.R
import com.umpa2020.tracer.roomDatabase.viewModel.RecordViewModel
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.DISTANCE_POINT
import com.umpa2020.tracer.constant.Constants.Companion.FINISH_POINT
import com.umpa2020.tracer.constant.Constants.Companion.START_POINT
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.MM_SS
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.makingIcon
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.util.*
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_running.*
import kotlin.time.milliseconds


class RunningActivity : BaseRunningActivity() {
  private lateinit var recordViewModel: RecordViewModel
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_running)
    supportActionBar?.title = "RUNNING"
    init()

    // TODO : 여기 밑에 있는 함수가 init()가 실행되야 가능한데 아애 init()에 넣어두는건?
    // TODO : 그래서 RankingRecodeRacingActivity init()에서는 그렇게 해봄
    notice(getString(R.string.start_running))

    TTS.speech(getString(R.string.pushthestartbutton))
  }

  override fun init() {
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)

    startButton = runningStartButton
    stopButton = runningStopButton
    pauseButton = runningPauseButton
    notificationTextView = runningNotificationTextView
    pauseNotificationTextView = runningPauseNotificationTextView
    drawerHandle = runningHandle
    drawer = runningDrawer
    chronometer = runningTimerTextView
    speedTextView=runningSpeedTextView
    distanceTextView=runningDistanceTextView
    /**
    Stop 팝업 띄우기
     */
    stopButton.setOnLongClickListener {
      if (distance < Constants.MINIMUM_STOPPING_DISTANCE) {
        noticePopup = ChoicePopup(this, getString(R.string.please_select),
          getString(R.string.twohundred_save),
          getString(R.string.yes), getString(R.string.no),
          View.OnClickListener {
            noticePopup.dismiss()
            // yes 버튼 눌렀을 때 해당 액티비티 재시작.
            lockScreen(false)
            finish()
          },
          View.OnClickListener {
            noticePopup.dismiss()
          })
        noticePopup.show()
      } else
        stop()
      true
    }
    super.init()
  }

  var cameraZoomSize = 0.0f
  override fun start() {
    super.start()

   traceMap.mMap.addMarker(MarkerOptions()
     .zIndex(3.4f)
     .position(currentLatLng)
     .title("Start")
     .icon( R.drawable.ic_start_point.makingIcon()))
    TTS.speech(getString(R.string.startRunning))

    traceMap.mMap.setOnCameraMoveListener {
      Logg.i(traceMap.mMap.cameraPosition.zoom.toString())
      cameraZoomSize = traceMap.mMap.cameraPosition.zoom
    }
    wpList.add(
      WayPoint.builder()
        .lat(currentLatLng.latitude)
        .lon(currentLatLng.longitude)
        .name("Start")
        .desc("Start Description")
        .time(System.currentTimeMillis())
        .type(START_POINT)
        .build()
    )
  }

  override fun stop() {
    super.stop()

    // stop tts 설정
    TTS.speech(getString(R.string.finishRunning))

    wpList.add(
      WayPoint.builder()
        .lat(currentLatLng.latitude)
        .lon(currentLatLng.longitude)
        .name("Finish")
        .desc("Finish Description")
        .time(System.currentTimeMillis())
        .type(FINISH_POINT)
        .build()
    )
    val infoData = InfoData()
    infoData.distance = distance
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.privacy = privacy
    infoData.startLatitude = trkList.first().latitude.toDouble()
    infoData.startLongitude = trkList.first().longitude.toDouble()
    val routeGPX = RouteGPX(infoData.time.toString(), "", wpList, trkList)

    val intent = Intent(this, RunningSaveActivity::class.java)
    intent.putExtra("RouteGPX", routeGPX)
    intent.putExtra("InfoData", infoData)
    startActivity(intent)
    finish()
  }

  override fun updateLocation(curLoc: Location) {
    super.updateLocation(curLoc)
    //WPINTERVAL마다 waypoint 추가
    if (userState == UserState.RUNNING) {
      if (distance.toInt() / Constants.WPINTERVAL >= markerCount) {
        if (distance > 0) markerCount = distance.toInt() / Constants.WPINTERVAL
        traceMap.mMap.addMarker(
          MarkerOptions()
            .position(currentLatLng)
            .title(markerCount.toString())
            .icon(passedIcon)
            .anchor(0f,0.5f)
        )
        wpList.add(
          WayPoint.builder()
            .lat(currentLatLng.latitude)
            .lon(currentLatLng.longitude)
            .name("WayPoint")
            .desc("wayway...")
            .time((System.currentTimeMillis()))
            .type(DISTANCE_POINT)
            .build()
        )
        markerCount++
      }
    }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.runningStartButton -> {
        start()
      }
      R.id.runningPauseButton -> {
        if (privacy == Privacy.RACING) {
          showPausePopup(
            "일시정지를 하게 되면\n경쟁 모드 업로드가 불가합니다.\n\n일시정지를 하시겠습니까?"
          )
        } else {
          if (userState == UserState.PAUSED)
            restart()
          else
            pause()
        }
      }
      R.id.runningStopButton -> {
        Toast.makeText(this, "종료를 원하시면 길게 눌러주세요", Toast.LENGTH_LONG).show()
      }
    }
  }
}
