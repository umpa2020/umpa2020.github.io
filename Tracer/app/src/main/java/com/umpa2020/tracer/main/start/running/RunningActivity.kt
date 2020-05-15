package com.umpa2020.tracer.main.start.running

import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.SupportMapFragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.toWayPoint
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.TTS
import kotlinx.android.synthetic.main.activity_running.*


class RunningActivity : BaseRunningActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_running)
    supportActionBar?.title = "RUNNING"
    init()
    notice(getString(R.string.start_running))
    TTS.speech(getString(R.string.pushthestartbutton))
  }

  /**
   * BaseRunning 에서 업데이트 해줘야 하는 View 할당
   * stop 리스너 설정
   */
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
    speedTextView = runningSpeedTextView
    distanceTextView = runningDistanceTextView
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

  /**
   * 러닝 이 시작될 때
   */
  override fun start() {
    super.start()

    wpList.add(currentLocation.toWayPoint(START_POINT))
    traceMap.addMarker(wpList.first())
    TTS.speech(getString(R.string.startRunning))
  }

  /**
   * 러닝이 종료될 때
   * 현재 위치를 finish point로 설정
   * InfoData와 RouteGPX를 생성해서 RunningSaveActivity에게 전달함
   */
  override fun stop() {
    super.stop()
    TTS.speech(getString(R.string.finishRunning))

    wpList.add(currentLocation.toWayPoint(FINISH_POINT))
    val infoData = InfoData()
    infoData.distance = distance
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.startLatitude = trkList.first().lat
    infoData.startLongitude = trkList.first().lon
    val routeGPX = RouteGPX(infoData.time!!, "", wpList, trkList)

    val intent = Intent(this, RunningSaveActivity::class.java)
    intent.putExtra("RouteGPX", routeGPX)
    intent.putExtra("InfoData", infoData)
    startActivity(intent)
    finish()
  }

  /**
   * 위치가 업데이트 되면, 거리를 측정해 distance Point 생성
   */
  override fun updateLocation(curLoc: Location) {
    super.updateLocation(curLoc)
    //WPINTERVAL마다 waypoint 추가
    if (userState == UserState.RUNNING) {
      if (distance.toInt() / Constants.WPINTERVAL >= markerCount) {
        if (distance > 0) markerCount = distance.toInt() / Constants.WPINTERVAL
        wpList.add(curLoc.toWayPoint(DISTANCE_POINT))
        traceMap.addMarker(wpList.last())
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
          showPausePopup(getString(R.string.pause_mode))
        } else {
          if (userState == UserState.PAUSED) restart()
          else pause()
        }
      }
      R.id.runningStopButton -> {
        Toast.makeText(this, getString(R.string.press_hold), Toast.LENGTH_LONG).show()
      }
    }
  }
}
