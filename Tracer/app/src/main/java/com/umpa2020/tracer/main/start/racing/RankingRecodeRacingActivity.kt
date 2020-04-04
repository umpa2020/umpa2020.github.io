package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.main.start.BaseActivity
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
import kotlin.math.roundToLong

class RankingRecodeRacingActivity : BaseActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
  OnDrawerCloseListener {
  var TAG = "what u wanna say?~~!~!"       //로그용 태그
  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapTitle: String
  lateinit var increaseExecuteThread: Thread
  lateinit var chronometer: Chronometer
  var timeWhenStopped: Long = 0
  var racingResult = true
  var deviationFlag = false
  var deviationCount = 0
  var stopPopup: ChoicePopup? = null

  var wptList: MutableList<WayPoint> = mutableListOf()
  var track: MutableList<LatLng> = mutableListOf()
  var nextWP: Int = 0
  lateinit var loadTrack: Polyline

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    setContentView(R.layout.activity_ranking_recode_racing)

    mapRouteGPX = intent.getParcelableExtra("RouteGPX") as RouteGPX
    mapTitle = intent.getStringExtra("mapTitle")
    init()
    racingControlButton.setOnLongClickListener {
      if (userState == UserState.RUNNING) {
        stopPopup = ChoicePopup(this, "선택해주세요.",
          "지금 정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
          "예", "아니오",
          View.OnClickListener {
            // yes 버튼 눌렀을 때 해당 액티비티 재시작.
            finish()
          },
          View.OnClickListener {
            stopPopup!!.dismiss()
          })
        stopPopup!!.show()
      } else {
        //stop(true)
      }
      true
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)
    traceMap = TraceMap(googleMap)
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    traceMap.drawRoute(track,mapRouteGPX.wptList)
  }

  private fun increaseExecute(mapTitle: String) {

    increaseExecuteThread = Thread(Runnable {
      val db = FirebaseFirestore.getInstance()

      db.collection("mapInfo").document(mapTitle)
        .update("execute", FieldValue.increment(1))
        .addOnSuccessListener { Logg.d("DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Logg.w("Error updating document$e") }
    })

    increaseExecuteThread.start()
  }

  private fun init() {
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)
    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)
    chronometer = racingTimerTextView
    loadRoute()
  }

  fun loadRoute() {
    mapRouteGPX.trkList.forEach {
      track.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
    }
    wptList=mapRouteGPX.wptList
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.racingControlButton -> {
        when (userState) {
          //TODO:notice " you should be in 200m"
          UserState.NORMAL -> {
            //200m 안으로 들어오세요!
            Logg.d("NORMAL")
          }
          UserState.READYTORACING -> {
            Logg.d("READYTORACING")
            start()
          }
          UserState.RUNNING -> {
            Logg.d("RUNNING")
            //stop(false)
          }
          else -> {

          }
        }
      }
      R.id.racingNotificationButton -> {
        racingNotificationLayout.visibility = View.GONE
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
    }
  }

  override fun start() {
    super.start()
    increaseExecute(mapTitle)
    racingNotificationLayout.visibility = View.GONE
    racingControlButton.text = "Stop"
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.start()
  }

  override fun stop() {
    super.stop()
    chronometer.stop()

    val infoData = InfoData()
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.mapTitle = mapTitle
    // TODO: 거리 계산 해야함!!!
    infoData.distance = 10.0000
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
      traceMap.changeMarkerColor(nextWP,BitmapDescriptorFactory.HUE_BLUE)
      nextWP++
      if (nextWP == mapRouteGPX.wptList.size) {
        stop()
      }
    }
  }

  private fun checkDeviation() {
    //경로이탈검사
    if (PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      )
    ) {//경로 안에 있으면
      if (deviationFlag) {
        deviationFlag = false
        deviationCount = 0
      }
    } else {//경로 이탈이면
      deviationCount++
      //TODO : notice deviation
    }
  }

  private fun checkIsReadyToRacing() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) > Constants.ARRIVE_BOUNDARY
    ) {
      userState = UserState.BEFORERACING
      traceMap.changeMarkerColor(0,BitmapDescriptorFactory.HUE_ROSE)
    } else {
      traceMap.changeMarkerColor(0,BitmapDescriptorFactory.HUE_BLUE)
      racingNotificationButton.text = "시작을 원하시면 START를 누르세요"
    }
  }

  private fun checkIsReady() {
    racingNotificationButton.text =
      ("시작 포인트로 이동하십시오.\n시작포인트까지 남은거리\n"
        + (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        wptList[0].toLatLng()
      )).roundToLong().toString() + "m")
    //시작포인트에 10m이내로 들어오면 준비상태로 변경
    Logg.d(
      SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ).toString()
    )
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,mapRouteGPX.wptList[0].toLatLng()
      ) <= 10
    ) {
      userState = UserState.READYTORACING
    }
  }

  override fun onScrollStarted() {
    Logg.d("onScrollStarted()")
  }

  override fun onScrollEnded() {
    Logg.d("onScrollEnded()")
  }

  override fun onDrawerOpened() {
    racingHandle.text = "▼"
    Logg.d("onDrawerOpened()")
  }

  override fun onDrawerClosed() {
    racingHandle.text = "▲"
    Logg.d("onDrawerClosed()")
  }
}
