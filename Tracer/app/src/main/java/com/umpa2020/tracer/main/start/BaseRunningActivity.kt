package com.umpa2020.tracer.main.start

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.view.animation.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.broadcastReceiver.LocationBroadcastReceiver
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.DRAWER_DURATION
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.DistanceTimeData
import com.umpa2020.tracer.dataClass.TimeData
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType.TRACK_POINT
import com.umpa2020.tracer.lockscreen.util.LockScreen
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.main.MainActivity.Companion.locationViewModel
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.util.*
import kotlinx.android.synthetic.main.activity_running.*
import java.lang.String.format
import java.util.*


open class BaseRunningActivity : BaseActivity(), OnMapReadyCallback, OnSingleClickListener {
  lateinit var traceMap: TraceMap
  var privacy = Privacy.RACING
  var currentLocation = Location(LocationManager.GPS_PROVIDER).apply {
    latitude = UserInfo.lat.toDouble()
    longitude = UserInfo.lng.toDouble()
  }
  var distance = 0.0
  var time = 0.0
  var previousLatLng = LatLng(0.0, 0.0)          //이전위
  var currentLatLng = LatLng(37.619742, 127.060836)
  var elevation = 0.0
  var speed = 0.0

  var userState = UserState.NORMAL       //사용자의 현재상태 달리기
  var moving = false  //유저가 움직인건지 gps가 튄건지 잡는 flag
  var trkList: MutableList<WayPoint> = mutableListOf()  //사용자의 이동 경로 리스트
  var wpList: MutableList<WayPoint> = mutableListOf()   //사용자의 체크포인트 리스트
  var markerCount = 1   //현재 찍힌 마커의 개수
  var timeWhenStopped: Long = 0   //일시정지된 시간

  var wedgedCamera = true // 사용자 카메라 인식 flag
  lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

  companion object {
    var zoomLevel: Float? = 16f // 줌 레벨 할당
  }

  //  var zoomLevel: Float? = 16f // 줌 레벨 할당
  lateinit var progressBar: MyProgressBar

  open fun init() {
    runningHandle.setOnClickListener(this)
    runningStartButton.setOnClickListener(this)
    runningStopButton.setOnClickListener(this)
    runningPauseButton.setOnClickListener(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    traceMap = TraceMap(googleMap) //구글맵

    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    traceMap.mMap.uiSettings.isCompassEnabled = true
    traceMap.mMap.uiSettings.isZoomControlsEnabled = true

    traceMap.mMap.setMaxZoomPreference(18.0f) // 최대 줌 설정
    // startFragment의 마지막 위치를 가져와서 카메라 설정
    val latLng = LatLng(UserInfo.lat.toDouble(), UserInfo.lng.toDouble())
    traceMap.initCamera(latLng)

    traceMap.mMap.setOnCameraMoveStartedListener {
      // 공개 정적 최종 정수 REASON_GESTURE, 지도에서 사용자의 제스처에 응답하여 카메라 동작이 시작되었습니다.
      // 예를 들어, 이동, 기울기, 핀치 확대 또는 회전.
      if (it == 1 || it == 3) {
        wedgedCamera = false
      }
    }

    // 지도가 멈추면 줌 비율 얻어오기
    traceMap.mMap.setOnCameraIdleListener {
      zoomLevel = if (traceMap.mapDownFlag) {
        traceMap.mapDownFlag = false
        16F
      } else {
        traceMap.mMap.cameraPosition.zoom
      }
    }

    // 내 위치 버튼 클릭 리스너
    goToMyPosition.setOnClickListener {
      Logg.d("줌 레벨 : $zoomLevel")
      traceMap.moveCamera(currentLocation.toLatLng(), zoomLevel!!)
      wedgedCamera = true
    }

    goToMapButton.setOnClickListener{
      traceMap.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(traceMap.trackBounds, 1080, 300, 20))
      traceMap.mapDownFlag = true
    }
  }

  // 위치를 브로드케스트에서 받아 지속적으로 업데이트
  open fun updateLocation(curLoc: Location) {

    currentLocation = curLoc
    runningDistanceTextView.text = distance.prettyDistance
    runningSpeedTextView.text = speed.prettySpeed()

    // room DB에 속도, 거리 데이터 업데이트.
//    recordViewModel.updateSpeedDistance(speed.lockSpeed, distance.lockDistance)
    locationViewModel.setDistanceSpeed(DistanceTimeData(distance.lockDistance, speed.lockSpeed))
    if (setLocation(curLoc)) {
      when (userState) {
        UserState.NORMAL -> {
        }
        UserState.READYTORUNNING -> {
        }
        UserState.RUNNING -> {
          distance += SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng)
          traceMap!!.drawPolyLine(previousLatLng, currentLatLng)
          //tplist에 추가
          trkList.add(curLoc.toWayPoint(TRACK_POINT))
        }
        UserState.PAUSED -> {
        }
        UserState.STOP -> {
        }
        UserState.BEFORERACING -> {
        }
        UserState.READYTORACING -> {
        }
      }
    }
  }

  private var countDownTimer: CountDownTimer? = null
  private var ttsTimeCount=1L
  open fun start(tts: String) {
    userState = UserState.STOP
    countDownTextView.visibility = View.VISIBLE
    // 시작 카운트 다운
    countDownTimer = object : CountDownTimer(Constants.MILLISINFUTURE, Constants.COUNTDOWN_INTERVAL) {
      override fun onTick(millisUntilFinished: Long) {


        // 3..2..1..로 보여주기 위해서 + 1
        countDownTextView.text =
          format(Locale.getDefault(), "%d", millisUntilFinished / Constants.COUNTDOWN_INTERVAL + 1)
      }

      override fun onFinish() {
        countDownTextView.visibility = View.GONE
        userState = UserState.RUNNING
        buttonAnimation()

        isOpend = false
        slidingDrawer()
        runningTimerTextView.base = SystemClock.elapsedRealtime()
        runningTimerTextView.start()
        runningTimerTextView.setOnChronometerTickListener {
          val a=SystemClock.elapsedRealtime() - it.base
          if(a/300000==ttsTimeCount){
            ttsTimeCount++
            TTS.speech(getString(R.string.running_tts_regular_time,(ttsTimeCount*5).toString(),runningDistanceTextView.text,runningSpeedTextView.text))
          }
        }
        runningNotificationTextView.visibility = View.GONE
        lockScreen(true)
        // viewModel 초기값 설정 => 시작 시간 설정
        locationViewModel.setTimes(TimeData(runningTimerTextView.base, true, 0L, ""))
        TTS.speech(tts)
      }
    }.start()
  }

  open fun pause() {
    privacy = Privacy.PUBLIC
    userState = UserState.PAUSED

    timeWhenStopped = runningTimerTextView.base - SystemClock.elapsedRealtime()

    // 시간 텍스트 설정, 시간 통제 업데이트
    locationViewModel.setTimes(TimeData(0L, false, timeWhenStopped, runningTimerTextView.text.toString()))

    runningTimerTextView.stop()
    runningPauseButton.text = getString(R.string.restart)

    pauseNotice(getString(R.string.notece_msg_stop_tracking))
  }

  open fun restart() {
    userState = UserState.RUNNING
    runningPauseButton.text = getString(R.string.pause)
    val restartTime = SystemClock.elapsedRealtime()
    runningTimerTextView.base = restartTime + timeWhenStopped

    // 시간 통제 업데이트, 재시작 업데이트
    locationViewModel.setTimes(TimeData(restartTime, true, timeWhenStopped, ""))

    runningTimerTextView.start()

    runningPauseNotificationTextView.invisible()
    disappearAnimation()
  }

  open fun stop(tts: String) {
    userState = UserState.STOP

    // 시간 통제 업데이트
    locationViewModel.setTimes(TimeData(0L, false, 0L, ""))

    runningTimerTextView.stop()
    TTS.speech(tts)
    lockScreen(false)
  }

  private fun pauseNotice(str: String) {
    runningPauseNotificationTextView.visible()
    runningPauseNotificationTextView.text = str
    appearAnimation()
  }

  fun notice(str: String) {
    runningNotificationTextView.visibility = View.VISIBLE
    runningNotificationTextView.text = str
  }

  private fun setLocation(location: Location): Boolean {//현재위치를 이전위치로 변경
    elevation = location.altitude
    speed = location.speed.toDouble()

    previousLatLng = currentLatLng
    currentLatLng = location.toLatLng()
    if (previousLatLng == currentLatLng) {
      moving = false
    } else {
      moving = true

      if (wedgedCamera) { // 사용자 카메라 인식 flag
        // 유저 상태에 따라 카메라 설정
        if (userState == UserState.NORMAL) {
//
          traceMap.moveCamera(location.toLatLng(), zoomLevel!!)
        } else if (userState == UserState.RUNNING) {
//
          traceMap.moveCameraUserDirection(location, zoomLevel!!)
        }
      }
    }
    return moving
  }

  fun lockScreen(flag: Boolean) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.instance.context())
    if (prefs.getBoolean("LockScreenStatus", true)) {
      if (flag) {

        LockScreen.active()
      } else {

        LockScreen.deActivate()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_running)
    init()
    locationBroadcastReceiver = LocationBroadcastReceiver(this)
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))

    // 거리, 속도, 시간 관련 데이터 초기 설정.
    locationViewModel.init(DistanceTimeData("0.0", "0.0"), TimeData(0L, false, 0L, "0.0"))
  }

  override fun onPause() {
    super.onPause()
    TTS.speechStop()
    countDownTimer?.cancel()
  }


  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBroadcastReceiver)
    // Shared에 마지막 위치 업데이트
    UserInfo.lat = currentLocation.latitude.toFloat()
    UserInfo.lng = currentLocation.longitude.toFloat()

  }

  lateinit var noticePopup: ChoicePopup
  override fun onBackPressed() {
    when (userState) {
      UserState.RUNNING, UserState.PAUSED -> {
        noticePopup = ChoicePopup(this, getString(R.string.please_select),
          getString(R.string.stop_save),
          getString(R.string.yes), getString(R.string.no),
          View.OnClickListener {
            noticePopup.dismiss()
            lockScreen(false)
            startActivity(Intent(this, MainActivity::class.java).apply {
              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
            finish()
          },
          View.OnClickListener {
            noticePopup.dismiss()
          })
        noticePopup.show()
      }
      else -> {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java).apply {
          addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
      }
    }
  }

  fun showPausePopup(text: String) {
    noticePopup = ChoicePopup(this,
      getString(R.string.please_select),
      text,
      getString(R.string.yes), getString(R.string.no),
      View.OnClickListener {
        //Yes 버튼 눌렀을 때
        runningNotificationTextView.visibility = View.GONE
        noticePopup.dismiss()
        pause()
      },
      View.OnClickListener {
        // No 버튼 눌렀을 때
        noticePopup.dismiss()
      })
    noticePopup.show()
  }

  /**
   * 레이아웃이 스르륵 보이는 함수
   */

  private fun appearAnimation() {
    val height = runningPauseNotificationTextView.height.toFloat()


    val translationAnimation1 = TranslateAnimation(0f, 0f, 0f, height)
    val alphaAnimate = AlphaAnimation(0f, 1f) //투명도 변화
    alphaAnimate.duration = Constants.PAUSE_ANIMATION_DURATION_TIME
    alphaAnimate.repeatMode = Animation.REVERSE
    alphaAnimate.repeatCount = Animation.INFINITE


    val animationSet = AnimationSet(true)
    animationSet.addAnimation(translationAnimation1)
    animationSet.addAnimation(alphaAnimate)

    // https://medium.com/@gus0000123/android-animation-interpolar-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-8d228f4fc3c3
    animationSet.interpolator = AccelerateDecelerateInterpolator()
    animationSet.fillAfter = true
    animationSet.fillBefore = true
    animationSet.duration = Constants.PAUSE_ANIMATION_DURATION_TIME
    runningPauseNotificationTextView.animation = animationSet
    runningPauseNotificationTextView.animation.start()
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  private fun disappearAnimation() {
    val height = runningPauseNotificationTextView.height.toFloat()
    runningPauseNotificationTextView.clearAnimation() // 일시정지 애니메이션 종료
    runningPauseNotificationTextView.invisible()


    val translationAnimation1 = TranslateAnimation(0f, 0f, height, 0f)
    translationAnimation1.duration = Constants.PAUSE_ANIMATION_DURATION_TIME
    runningPauseNotificationTextView.startAnimation(translationAnimation1)
  }

  /**
   *  버튼 하나에서 두개로 퍼지는 애니메니션
   */
  private fun buttonAnimation() {
    val fabOpen =
      AnimationUtils.loadAnimation(applicationContext, R.anim.running_btn_open) // 애니매이션 초기화
    runningStartButton.visibility = View.INVISIBLE
    runningStopButton.startAnimation(fabOpen)
    runningPauseButton.startAnimation(fabOpen)
    runningStopButton.isClickable = true
    runningPauseButton.isClickable = true
  }

  /**
   *  러닝 기록 측정 SlidingDrawer 애니메이션
   */
  var isOpend = true
  private fun slidingDrawer() {
    if (isOpend) { // 내려가기
      ObjectAnimator.ofFloat(runningDrawer, "translationY", content.height.toFloat()).apply {
        duration = DRAWER_DURATION
        start()
      }
      runningHandle.text = "▲"
      isOpend = false
    } else { // 올라가기
      ObjectAnimator.ofFloat(runningDrawer, "translationY", 0f).apply {
        duration = DRAWER_DURATION
        start()
      }
      runningHandle.text = "▼"
      isOpend = true
    }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.runningHandle -> {

        slidingDrawer()
      }
    }
  }
}
