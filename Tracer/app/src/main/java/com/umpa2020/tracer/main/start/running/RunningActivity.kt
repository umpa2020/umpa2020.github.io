package com.umpa2020.tracer.main.start.running

import android.animation.ObjectAnimator
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.maps.SupportMapFragment
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.addDirectionSign
import com.umpa2020.tracer.extensions.classToGpx
import com.umpa2020.tracer.extensions.toWayPoint
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.TTS
import kotlinx.android.synthetic.main.activity_running.*
import java.io.File


class RunningActivity : BaseRunningActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportActionBar?.title = "RUNNING"
    notice(getString(R.string.start_running))
    TTS.speech(getString(R.string.pushthestartbutton))
  }

  /**
   * BaseRunning 에서 업데이트 해줘야 하는 View 할당
   * stop 리스너 설정
   */
  override fun init() {
    super.init()
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)

    /**
    Stop 팝업 띄우기
     */
    runningStopButton.setOnLongClickListener {
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
        stop(getString(R.string.finishRunning))
      true
    }
  }

  /**
   * 러닝 이 시작될 때
   */
  override fun start(tts: String) {
    super.start(tts)
    wpList.add(currentLocation.toWayPoint(START_POINT))
    traceMap.addMarker(wpList.first())
  }

  /**
   * 러닝이 종료될 때
   * 현재 위치를 finish point로 설정
   * InfoData와 RouteGPX를 생성해서 RunningSaveActivity에게 전달함
   */

  override fun stop(tts: String) {
    super.stop(tts)

    wpList.add(currentLocation.toWayPoint(FINISH_POINT))
    val infoData = MapInfo()
    infoData.distance = distance
    infoData.time = SystemClock.elapsedRealtime() - runningTimerTextView.base
    infoData.startLatitude = trkList.first().lat
    infoData.startLongitude = trkList.first().lon
    infoData.challenge = false
    val routeGPX = RouteGPX(infoData.time, "", wpList, trkList)

    val saveFolder = File(App.instance.filesDir, "RouteGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    routeGPX.addDirectionSign()
    val routeGpxUri = routeGPX.classToGpx(saveFolder.path).toString()

    val intent = Intent(this, RunningSaveActivity::class.java)
    intent.putExtra(RacingActivity.ROUTE_GPX, routeGpxUri)
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
    super.onSingleClick(v)
    when (v!!.id) {
      R.id.runningStartButton -> {
        start(getString(R.string.startRunning)) // tts String 전달
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
