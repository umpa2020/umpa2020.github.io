package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION
import com.umpa2020.tracer.constant.Constants.Companion.INFOUPDATE
import com.umpa2020.tracer.constant.Constants.Companion.RACINGFINISH
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.main.start.BaseActivity
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.trace.decorate.BasicMap
import com.umpa2020.tracer.trace.decorate.PolylineDecorator
import com.umpa2020.tracer.trace.decorate.RacingDecorator
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyHandler
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*

class RankingRecodeRacingActivity : BaseActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
  OnDrawerCloseListener {
  var TAG = "what u wanna say?~~!~!"       //로그용 태그
  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapTitle: String
  lateinit var increaseExecuteThread: Thread
  lateinit var chronometer: Chronometer
  var timeWhenStopped: Long = 0

  private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

  var stopPopup : ChoicePopup? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    setContentView(R.layout.activity_ranking_recode_racing)

    mapRouteGPX = intent.getParcelableExtra("RouteGPX") as RouteGPX
    mapTitle = intent.getStringExtra("mapTitle")
    init()
    //locationBroadcastReceiver = LocationBroadcastReceiver(traceMap)
    racingControlButton.setOnLongClickListener {
      if (userState == UserState.RUNNING) {
        stopPopup = ChoicePopup(this, "선택해주세요.",
          "지금 정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?",
          "예","아니오",
          View.OnClickListener {
            // yes 버튼 눌렀을 때 해당 액티비티 재시작.
            finish()
          },
          View.OnClickListener {
            stopPopup!!.dismiss()
          })
        stopPopup!!.show()
      }else{
        //stop(true)
      }
      true
    }
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

  //경로이탈 되는지 확인
  var mHandler = Handler(App.instance.mainLooper) {
    when (it.what) {
      INFOUPDATE -> {
        racingDistanceTextView.text = distance.toString()
        racingSpeedTextView.text = speed.toString()
      }
      RACINGFINISH -> {
       // stop(true)
      }
      DEVIATION -> {
        Logg.d((it.obj as Int).toString())
        //TODO:pop up 노티스
        if (it.obj as Int > Constants.DEVIATION_COUNT) {
          //stop(false)
        }
      }
      else -> {
      }
    }
    true
  }

  private fun init() {
    MyHandler.myHandler = mHandler
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    traceMap = TraceMap(smf, this)
    routeGPX = mapRouteGPX
    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)
    chronometer = racingTimerTextView
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
/*
  fun start() {
    increaseExecute(mapTitle)
    racingNotificationLayout.visibility = View.GONE
    racingControlButton.text = "Stop"
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.start()
    traceMap.start()
  }*/
/*
  fun stop(result: Boolean) {

    val routeGPX = traceMap.stop(SystemClock.elapsedRealtime() - chronometer.base)
    timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
    traceMap.stop(SystemClock.elapsedRealtime() - chronometer.base)
    chronometer.stop()

    val infoData = InfoData()
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.mapTitle = mapTitle
    // TODO: 거리 계산 해야함!!!
    infoData.distance = 10.0000
    //infoData.distance = calcLeftDistance()

    val newIntent = Intent(this, RacingFinishActivity::class.java)
    newIntent.putExtra("Result", result)
    newIntent.putExtra("InfoData", infoData)
    newIntent.putExtra("MapRouteGPX", mapRouteGPX)
    newIntent.putExtra("RouteGPX", routeGPX)

    startActivity(newIntent)
    finish()
  }*/

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
