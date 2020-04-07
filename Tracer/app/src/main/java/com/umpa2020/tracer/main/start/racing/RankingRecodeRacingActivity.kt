package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.ARRIVE_BOUNDARY
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION_COUNT
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.network.FBMap
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
import kotlin.math.roundToLong

class RankingRecodeRacingActivity : BaseRunningActivity() {
  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapTitle: String
  var racingResult = true
  var deviationCount = 0

  var wptList: MutableList<WayPoint> = mutableListOf()
  var track: MutableList<LatLng> = mutableListOf()
  var nextWP: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    setContentView(R.layout.activity_ranking_recode_racing)
    mapRouteGPX = intent.getParcelableExtra("RouteGPX") as RouteGPX
    mapTitle = intent.getStringExtra("mapTitle")!!

    init()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)
    traceMap.drawRoute(track, mapRouteGPX.wptList)
  }


  override fun init() {
    loadRoute()
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)

    startButton = racingStartButton
    stopButton = racingStopButton
    pauseButton = racingPauseButton
    chronometer = racingTimerTextView
    notificationTextView = racingNotificationTextView
    pauseNotificationTextView = racingPauseNotificationTextView
    drawerHandle = racingHandle
    drawer = racingDrawer
    pauseNotice("기록 측정 중지")
    stopButton.setOnLongClickListener {
      noticePopup = ChoicePopup(this, "선택해주세요.",
        "지금 정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
        "예", "아니오",
        View.OnClickListener {
          noticePopup.dismiss()
          finish()
        },
        View.OnClickListener {
          noticePopup.dismiss()
        })
      noticePopup.show()
      true
    }
  }

  fun loadRoute() {
    mapRouteGPX.trkList.forEach {
      track.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
    }
    wptList = mapRouteGPX.wptList
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.racingStartButton -> {
        when (userState) {
          UserState.NORMAL -> {
            Toast.makeText(this, "시작포인트 " + ARRIVE_BOUNDARY + "m 안에서만 시작할 수 있습니다", Toast.LENGTH_LONG).show()
            Logg.d("NORMAL")
          }
          UserState.READYTORACING -> {
            Logg.d("READYTORACING")
            notificationTextView.visibility = View.GONE
            start()
          }
          else -> {
          }
        }
      }
      R.id.racingStopButton -> {
        Toast.makeText(this, "종료를 원하시면 길게 눌러주세요", Toast.LENGTH_LONG).show()
      }
      R.id.racingPauseButton -> {
        if (privacy == Privacy.RACING) {
          showPausePopup(
            "일시정지를 하게 되면\n랭킹등록이 불가합니다.\n\n일시정지를 하시겠습니까?"
          )
        } else {
          if (userState == UserState.PAUSED)
            restart()
          else
            pause()
        }
      }
      R.id.racingNotificationButton -> {
        notificationTextView.visibility = View.GONE
      }
    }
  }

  override fun updateLocation(curLoc: Location) {
    super.updateLocation(curLoc)
    racingDistanceTextView.text = distance.toString()
    racingSpeedTextView.text = speed.toString()
    when (userState) {
      UserState.NORMAL -> {
        Logg.d("NORMAL")
        checkIsReady()
      }
      UserState.READYTORACING -> {
        Logg.d("READYTORACING")
        checkIsReadyToRacing()
      }
      UserState.RUNNING -> {
        Logg.d("RUNNING")
        checkMarker()
        checkDeviation()
      }
      else -> {

      }
    }
  }

  override fun start() {
    super.start()
    FBMap().increaseExecute(mapTitle)
  }

  override fun stop() {
    super.stop()

    val infoData = InfoData()
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.mapTitle = mapTitle
    infoData.distance = distance
    //infoData.distance = calcLeftDistance()
    val routeGPX = RouteGPX(infoData.time.toString(), "", wpList, trkList)

    val newIntent = Intent(this, RacingFinishActivity::class.java)
    newIntent.putExtra("Result", racingResult)
    newIntent.putExtra("InfoData", infoData)
    newIntent.putExtra("MapRouteGPX", mapRouteGPX)
    newIntent.putExtra("RouteGPX", routeGPX)

    startActivity(newIntent)
    finish()
  }

  private fun checkMarker() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        wptList[nextWP].toLatLng()
      ) < Constants.ARRIVE_BOUNDARY
    ) {
      traceMap.changeMarkerColor(nextWP, BitmapDescriptorFactory.HUE_BLUE)
      nextWP++
      if (nextWP == mapRouteGPX.wptList.size) {
        stop()
      }
    }
  }

  private fun checkDeviation() {
    Logg.d(
      PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      ).toString()
    )
    //경로이탈검사
    if (PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      )
    ) {
      deviationCount = 0
      notificationTextView.visibility = View.GONE
    } else {
      deviationCount++
      notice("경로이탈 " + deviationCount.toString() + "\n(주의)경로이탈이 " + DEVIATION_COUNT + "초 이상이되면 랭킹등록을 못합니다.")
      if (deviationCount > DEVIATION_COUNT) {
        racingResult = false
        stop()
      }
    }
  }


  private fun checkIsReadyToRacing() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) > Constants.ARRIVE_BOUNDARY
    ) {
      userState = UserState.NORMAL
      traceMap.changeMarkerColor(0, BitmapDescriptorFactory.HUE_ROSE)
      notice(
        "시작 포인트로 이동하십시오.\n시작포인트까지 남은거리\n"
          + (SphericalUtil.computeDistanceBetween(
          currentLatLng,
          wptList[0].toLatLng()
        )).roundToLong().toString() + "m"
      )
    }
  }

  //시작포인트에 10m이내로 들어오면 준비상태로 변경
  private fun checkIsReady() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) <= ARRIVE_BOUNDARY
    ) {
      notice("시작을 원하시면 START를 누르세요")
      traceMap.changeMarkerColor(0, BitmapDescriptorFactory.HUE_BLUE)
      userState = UserState.READYTORACING
    } else {
      notice(
        "시작 포인트로 이동하십시오.\n시작포인트까지 남은거리\n"
          + (SphericalUtil.computeDistanceBetween(
          currentLatLng,
          wptList[0].toLatLng()
        )).roundToLong().toString() + "m"
      )

    }
  }
}
