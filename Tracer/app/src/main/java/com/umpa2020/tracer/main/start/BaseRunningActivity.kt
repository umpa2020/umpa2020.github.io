package com.umpa2020.tracer.main.start

import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.*
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.lockscreen.util.LockScreen
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.roomDatabase.entity.MapRecordData
import com.umpa2020.tracer.roomDatabase.viewModel.RecordViewModel
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*


open class BaseRunningActivity : AppCompatActivity(), OnMapReadyCallback, OnDrawerScrollListener,
  OnDrawerOpenListener,
  OnDrawerCloseListener, OnSingleClickListener {
  lateinit var traceMap: TraceMap
  var privacy = Privacy.RACING
  var distance = 0.0
  var time = 0.0
  var previousLatLng = LatLng(0.0, 0.0)          //이전위
  var currentLatLng = LatLng(37.619742, 127.060836)

  var elevation = 0.0
  var speed = 0.0

  var userState = UserState.NORMAL       //사용자의 현재상태 달리기
  var moving = false
  var trkList: MutableList<WayPoint> = mutableListOf()
  var wpList: MutableList<WayPoint> = mutableListOf()
  var markerCount = 1
  var timeWhenStopped: Long = 0

  lateinit var chronometer: Chronometer
  lateinit var startButton: Button
  lateinit var stopButton: Button
  lateinit var pauseButton: Button
  lateinit var notificationTextView: TextView
  lateinit var pauseNotificationTextView: TextView
  lateinit var speedTextView: TextView
  lateinit var distanceTextView: TextView
  lateinit var drawerHandle: Button
  lateinit var drawer: SlidingDrawer

  private var wedgedCamera = true
  lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
  var unPassedIcon = R.drawable.ic_checkpoint_gray.makingIcon()
  var passedIcon = R.drawable.ic_checkpoint_red.makingIcon()


  private lateinit var recordViewModel: RecordViewModel
  open fun init() {
    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    startButton.setOnClickListener(this)
    pauseButton.setOnClickListener(this)
    stopButton.setOnClickListener(this)
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.

    traceMap.mMap.setOnCameraMoveListener {
      wedgedCamera = false
    }
    traceMap.mMap.setOnMyLocationButtonClickListener {
      wedgedCamera = true
      true
    }
    traceMap.mMap.uiSettings.isCompassEnabled=true
    traceMap.mMap.uiSettings.isZoomControlsEnabled=true
  }

  open fun updateLocation(curLoc: Location) {
    distanceTextView.text = distance.prettyDistance
    speedTextView.text = speed.prettySpeed()

    // room DB에 속도, 거리 데이터 업데이트.
    recordViewModel.updateSpeedDistance(speed.lockSpeed, distance.lockDistance)

    if (setLocation(curLoc)) {
      when (userState) {
        UserState.NORMAL -> {
          traceMap.initCamera(curLoc.toLatLng())
        }
        UserState.READYTORUNNING -> {
        }
        UserState.RUNNING -> {
          distance += SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng)
          traceMap.drawPolyLine(previousLatLng, currentLatLng)
          //tplist에 추가
          trkList.add(
            WayPoint.builder()
              .lat(currentLatLng.latitude)
              .lon(currentLatLng.longitude)
              .ele(elevation)
              .speed(speed)
              .name("track point")
              .build()
          )
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

  open fun start() {
    userState = UserState.RUNNING
    anim()

    Logg.d(chronometer.base.toString())
    chronometer.base = SystemClock.elapsedRealtime()

    // DB에 시작 시간 업데이트

    chronometer.start()
    notificationTextView.visibility = View.GONE
    lockScreen(true)

    // DB 초기값 설정
    val mapRecordDao = MapRecordData(
      0,
      "",
      "",
      chronometer.base,
      true,
      0L,
      ""
    )
    Logg.d("처음에 데이터 삽입?")

    recordViewModel.insert(mapRecordDao)
  }

  open fun pause() {
    Logg.i("일시 정지")

    privacy = Privacy.PUBLIC
    userState = UserState.PAUSED
    Logg.d(chronometer.text.toString())
    timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()

   recordViewModel.updateTimeText(chronometer.text.toString())
    // 시간 통제 업데이트
    recordViewModel.updateTimeControl(timeWhenStopped,false)
    chronometer.stop()
    pauseButton.text = getString(R.string.restart)

    pauseNotice("기록 측정 중지")
  }

  open fun restart() {
    userState = UserState.RUNNING
    pauseButton.text = getString(R.string.pause)
    val restartTime=SystemClock.elapsedRealtime()
    chronometer.base = restartTime + timeWhenStopped

    // 시간 통제 업데이트
    recordViewModel.updateTimeControl(timeWhenStopped,true)
    recordViewModel.updateStartTime(restartTime)
    chronometer.start()

    pauseNotificationTextView.visibility = View.INVISIBLE
    disappearAnimation()
  }

  open fun stop() {
    userState = UserState.STOP
    // 시간 통제 업데이트
    recordViewModel.updateTimeControl(0L,false)

    chronometer.stop()
    lockScreen(false)
  }

  fun pauseNotice(str: String) {
    pauseNotificationTextView.visibility = View.VISIBLE
    pauseNotificationTextView.text = str
    appearAnimation()
  }

  fun notice(str: String) {
    notificationTextView.visibility = View.VISIBLE
    notificationTextView.text = str
  }

  fun setLocation(location: Location): Boolean {//현재위치를 이전위치로 변경
    elevation = location.altitude
    speed = location.speed.toDouble()

    previousLatLng = currentLatLng
    currentLatLng = location.toLatLng()
    if (previousLatLng == currentLatLng) {
      moving = false
    } else if (false) { //TODO:비정상적인 움직임일 경우 + finish에 도착한 경우
    } else {
      moving = true
      if (wedgedCamera) traceMap.mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
    }
    return moving
  }

  fun lockScreen(flag: Boolean) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.instance.context())
    if (prefs.getBoolean("LockScreenStatus", false)) {
      if (flag) {
        Logg.d("서비스 실행")
        LockScreen.active()
      } else {
        Logg.d("서비스 중지")
        LockScreen.deActivate()
      }
    } else {
      Logg.d("LockScreen 설정 안함.")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    locationBroadcastReceiver = LocationBroadcastReceiver(this)
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))

    recordViewModel =
      RecordViewModel(this.application)
    recordViewModel.deleteAll()
  }
  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBroadcastReceiver)
  }
  override fun onPause() {
    super.onPause()
  }

  override fun onResume() {
    super.onResume()

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
            finish()
          },
          View.OnClickListener {
            noticePopup.dismiss()
          })
        noticePopup.show()
      }
      else -> {
        super.onBackPressed()
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
        notificationTextView.visibility = View.GONE
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

  fun appearAnimation() {
    Logg.i("일시정지 애니메이션")

    val height = pauseNotificationTextView.height.toFloat()
    Logg.i(height.toString())

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

    pauseNotificationTextView.animation = animationSet
    pauseNotificationTextView.animation.start()
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  fun disappearAnimation() {
    val height = pauseNotificationTextView.height.toFloat()
    pauseNotificationTextView.clearAnimation() // 일시정지 애니메이션 종료
    pauseNotificationTextView.visibility = View.GONE
    Logg.i("재시작 애니메이션")
    Logg.i(height.toString())
    val translationAnimation1 = TranslateAnimation(0f, 0f, height, 0f)
    translationAnimation1.duration = Constants.PAUSE_ANIMATION_DURATION_TIME
    pauseNotificationTextView.startAnimation(translationAnimation1)
  }

  /**
   *  버튼 하나에서 두개로 퍼지는 애니메니션
   */
  fun anim() {
    val fabOpen =
      AnimationUtils.loadAnimation(applicationContext, R.anim.running_btn_open) // 애니매이션 초기화
    startButton.visibility = View.INVISIBLE
    stopButton.startAnimation(fabOpen)
    pauseButton.startAnimation(fabOpen)
    stopButton.isClickable = true
    pauseButton.isClickable = true
  }

  override fun onScrollStarted() {
    Logg.d("onScrollStarted()")
  }

  override fun onScrollEnded() {
    Logg.d("onScrollEnded()")
  }

  override fun onDrawerOpened() {
    drawerHandle.text = "▼"
    Logg.d("onDrawerOpened() : ${drawerHandle.text}")
  }

  override fun onDrawerClosed() {
    drawerHandle.text = "▲"
    Logg.d("onDrawerClosed() : ${drawerHandle.text}")
  }

  override fun onSingleClick(v: View?) {
  }
}