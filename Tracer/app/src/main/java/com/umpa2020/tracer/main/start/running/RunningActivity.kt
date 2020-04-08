package com.umpa2020.tracer.main.start.running

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.util.Wow
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_running.*


class RunningActivity : BaseRunningActivity(), OnDrawerScrollListener, OnDrawerOpenListener, OnDrawerCloseListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_running)
    supportActionBar?.title = "RUNNING"
    init()

    // TODO : 여기 밑에 있는 함수가 init()가 실행되야 가능한데 아애 init()에 넣어두는건?
    // TODO : 그래서 RankingRecodeRacingActivity init()에서는 그렇게 해봄
    notice("시작버튼을 누르면 러닝이 시작됩니다")
    getString(R.string.pushthestartbutton)
    TTS.speech("push the start button")

  }

  override fun init() {
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)

    startButton = runningStartButton
    stopButton = runningStopButton
    pauseButton = runningPauseButton
    chronometer = runningTimerTextView
    notificationTextView = runningNotificationTextView
    pauseNotificationTextView = runningPauseNotificationTextView
    drawerHandle = runningHandle
    drawer = runningDrawer
    /**
    Stop 팝업 띄우기
     */
    stopButton.setOnLongClickListener {
      if (distance < 200) {
        noticePopup = ChoicePopup(this, "선택해주세요.",
          "거리가 200m 미만일때\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
          "예", "아니오",
          View.OnClickListener {
            noticePopup.dismiss()
            // yes 버튼 눌렀을 때 해당 액티비티 재시작.
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
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)
  }

  fun onClick(view: View) {
    when (view.id) {
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

  var cameraZoomSize = 0.0f
  override fun start() {
    super.start()

   traceMap.mMap.addMarker(MarkerOptions()
     .zIndex(3.4f)
     .position(currentLatLng)
     .title("Start")
     .icon( Wow.makingIcon(R.drawable.ic_racing_startpoint,this)))

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
        .build()
    )
  }

  override fun stop() {
    super.stop()
    wpList.add(
      WayPoint.builder()
        .lat(currentLatLng.latitude)
        .lon(currentLatLng.longitude)
        .name("Finish")
        .desc("Finish Description")
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
    runningDistanceTextView.text = distance.toString()
    //WPINTERVAL마다 waypoint 추가
    if (userState == UserState.RUNNING) {
      if (distance.toInt() / Constants.WPINTERVAL >= markerCount) {
        if (distance > 0) markerCount = distance.toInt() / Constants.WPINTERVAL
        traceMap.mMap.addMarker(
          MarkerOptions()
            .position(currentLatLng)
            .title(markerCount.toString())
            .icon(passedIcon)
        )
        wpList.add(
          WayPoint.builder()
            .lat(currentLatLng.latitude)
            .lon(currentLatLng.longitude)
            .name("WayPoint")
            .desc("wayway...")
            .build()
        )
        markerCount++
      }
    }
  }
}
