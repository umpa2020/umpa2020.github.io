package com.umpa2020.tracer.main.start.running

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.main.start.BaseActivity
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.Logg
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import io.jenetics.jpx.WayPoint
import kotlinx.android.synthetic.main.activity_running.*

class RunningActivity : BaseActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
  OnDrawerCloseListener {
  var B_RUNNIG = true
  lateinit var chronometer: Chronometer
  var timeWhenStopped: Long = 0
  var stopPopup: ChoicePopup? = null // 리스너 안에서 dismiss()를 호출하기 위해서 전역으로 선언

  // 버튼 에니메이션
  private var fabOpen: Animation? = null // Floating Animation Button

  private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_running)

    supportActionBar?.title = "RUNNING"

    init()

    /**
    Stop 팝업 띄우기
     */
    btn_stop!!.setOnLongClickListener {
      if (distance < 200) {
        stopPopup = ChoicePopup(this, "선택해주세요.",
          "거리가 200m 미만일때\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
          "예","아니오",
          View.OnClickListener {
            // yes 버튼 눌렀을 때 해당 액티비티 재시작.
            finish() // 액티비티가 끝나버리므로 dismiss()가 필요없다.
          },
          View.OnClickListener {
            stopPopup!!.dismiss()
          })
        stopPopup!!.show()
        //showChoicePopup("거리가 200m 미만일때\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?", NoticeState.STOP)
      } else
        stop()
      true
    }
  }

  private fun init() {
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    //running traceMap 선언 Polyline + Timer + Distance + Basic
    smf.getMapAsync(this)

    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)

    fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.running_btn_open) // 애니매이션 초기화
    chronometer = runningTimerTextView
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.btn_start -> {
        start()

      }
      R.id.btn_pause -> {
        if (privacy == Privacy.RACING) {
          showPausePopup(
            "일시정지를 하게 되면\n경쟁 모드 업로드가 불가합니다.\n\n일시정지를 하시겠습니까?"
          )
        } else {
          if (B_RUNNIG)
            pause()
          else
            restart()
        }

      }
      R.id.btn_stop -> {
        val text = "종료를 원하시면 길게 눌러주세요"
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
      }
    }
  }

  override fun start() {
    super.start()
    anim()
    //TODO:chronometer 클래스화하기
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.start()
    traceMap.mMap.addMarker(MarkerOptions().position(currentLatLng).title("Start"))
    wpList.add(
      WayPoint.builder()
        .lat(currentLatLng.latitude)
        .lon(currentLatLng.longitude)
        .name("Start")
        .desc("Start Description")
        .build()
    )
  }

  override fun pause() {
    super.pause()
    B_RUNNIG = false
    timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
    chronometer.stop()
    btn_pause.text = "재시작"
    //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed, 0, 0, 0)
  }

  override fun restart() { //TODO:Start with new polyline
    super.restart()
    btn_pause.text = "일시정지"
    //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed, 0, 0, 0)
    B_RUNNIG = true
    chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
    chronometer.start()
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


    //val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
    //formatter.timeZone = TimeZone.getTimeZone("UTC")

    val intent = Intent(this, RunningSaveActivity::class.java)
    intent.putExtra("RouteGPX", routeGPX)
    intent.putExtra("InfoData", infoData)
    startActivity(intent)
    finish()
  }

  override fun updateLocation(curLoc: Location) {
    super.updateLocation(curLoc)
    runningDistanceTextView.text=distance.toString()
    //100m마다 waypoint 추가
    if (userState == UserState.RUNNING) {
      if (distance.toInt() / Constants.WPINTERVAL >= markerCount) {
        if (distance > 0) markerCount = distance.toInt() / Constants.WPINTERVAL
        traceMap.mMap.addMarker(MarkerOptions().position(currentLatLng).title(markerCount.toString()))
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


  /**
   * 일시정지 팝업 띄우는 함수
   * */
  private var pausePopup: ChoicePopup? = null

  private fun showPausePopup(text: String) {
    pausePopup = ChoicePopup(this,
      "선택해주세요.",
      text,
      "예","아니오",
      View.OnClickListener {
        //Yes 버튼 눌렀을 때
        runningNotificationLayout.visibility = View.GONE
        pausePopup!!.dismiss()
        pause()
      },
      View.OnClickListener {
        // No 버튼 눌렀을 때
        pausePopup!!.dismiss()
      })
    pausePopup!!.show()
  }

  override fun onScrollStarted() {
    Logg.d("onScrollStarted()")
  }

  override fun onScrollEnded() {
    Logg.d("onScrollEnded()")
  }

  override fun onDrawerOpened() {
    //runningHandle.background = getDrawable(R.drawable.close_selector)
    runningHandle.text = "▼"
    Logg.d("onDrawerOpened()")
  }

  override fun onDrawerClosed() {
    //runningHandle.background = getDrawable(R.drawable.extend_selector)
    runningHandle.text = "▲"
    Logg.d("onDrawerClosed()")
  }

  /**
   *  버튼 하나에서 두개로 퍼지는 애니메니션
   */
  private fun anim() {
    btn_start.visibility = View.INVISIBLE
    btn_stop.startAnimation(fabOpen)
    btn_pause.startAnimation(fabOpen)
    btn_stop.isClickable = true
    btn_pause.isClickable = true
  }
}
