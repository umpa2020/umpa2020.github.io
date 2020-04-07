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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.Logg
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*


open class BaseRunningActivity : AppCompatActivity(), OnMapReadyCallback, OnDrawerScrollListener, OnDrawerOpenListener,
  OnDrawerCloseListener {
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
  lateinit var drawerHandle: Button
  lateinit var drawer: SlidingDrawer
  private var wedgedCamera = false
  lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

  open fun init() {
    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.

    traceMap.mMap.setOnCameraMoveListener {
      wedgedCamera=false
    }
    traceMap.mMap.setOnMyLocationButtonClickListener {
      wedgedCamera=true
      true
    }
  }

  open fun updateLocation(curLoc: Location) {
    if (setLocation(curLoc)) {
      when (userState) {
        UserState.NORMAL -> {
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
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.start()
    notificationTextView.visibility = View.GONE
  }

  var flag = true
  open fun pause() {
    Logg.i("일시 정지")

    privacy = Privacy.PUBLIC
    userState = UserState.PAUSED
    timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
    chronometer.stop()
    pauseButton.text = "재시작"

    pauseNotificationTextView.visibility = View.VISIBLE
    appearAnimation()
    //TODO:이거머에염?
    //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed, 0, 0, 0)
  }

  open fun restart() {

    userState = UserState.RUNNING
    pauseButton.text = "일시정지"
    //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed, 0, 0, 0)
    chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped

    chronometer.start()

    pauseNotificationTextView.visibility = View.INVISIBLE
    disappearAnimation()
  }

  open fun stop() {
    userState = UserState.STOP
    chronometer.stop()
  }

  fun pauseNotice(str: String) {
    pauseNotificationTextView.visibility = View.INVISIBLE
    pauseNotificationTextView.text = str
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

      if(wedgedCamera) traceMap.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16F))

    }
    return moving
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    locationBroadcastReceiver = LocationBroadcastReceiver(this)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBroadcastReceiver)
  }

  override fun onResume() {
    super.onResume()
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))
  }

  lateinit var noticePopup: ChoicePopup
  override fun onBackPressed() {
    when (userState) {
      UserState.RUNNING, UserState.PAUSED -> {
        noticePopup = ChoicePopup(this, "선택해주세요.",
          "지금 정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
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
      }
      else -> {
        super.onBackPressed()
      }
    }
  }

  fun showPausePopup(text: String) {
    noticePopup = ChoicePopup(this,
      "선택해주세요.",
      text,
      "예", "아니오",
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

    val translationAnimation1 = TranslateAnimation(0f,0f,0f,height)
    val alphaAnimate = AlphaAnimation(0f, 1f) //투명도 변화
    alphaAnimate.duration = Constants.PUASE_ANIMATION_DURATION_TIME
    alphaAnimate.repeatMode = Animation.REVERSE
    alphaAnimate.repeatCount = Animation.INFINITE


    val animationSet = AnimationSet(true)
    animationSet.addAnimation(translationAnimation1)
    animationSet.addAnimation(alphaAnimate)

    // https://medium.com/@gus0000123/android-animation-interpolar-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-8d228f4fc3c3
    animationSet.interpolator = AccelerateDecelerateInterpolator()
    animationSet.fillAfter = true
    animationSet.fillBefore = true
    animationSet.duration = Constants.PUASE_ANIMATION_DURATION_TIME

    pauseNotificationTextView.animation = animationSet
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  fun disappearAnimation() {
    val height = pauseNotificationTextView.height.toFloat()
//    pauseNotificationTextView.clearAnimation() // 일시정지 애니메이션 종료
    Logg.i("재시작 애니메이션")
    Logg.i(height.toString())
    
    val translationAnimation1 = TranslateAnimation(0f,0f,height,0f)
    translationAnimation1.duration = Constants.PUASE_ANIMATION_DURATION_TIME
    pauseNotificationTextView.startAnimation(translationAnimation1)
  }

  /**
   *  버튼 하나에서 두개로 퍼지는 애니메니션
   */
  fun anim() {
    val fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.running_btn_open) // 애니매이션 초기화
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
    racingHandle.text = "▼"
    Logg.d("onDrawerOpened()")
  }

  override fun onDrawerClosed() {
    racingHandle.text = "▲"
    Logg.d("onDrawerClosed()")
  }
}