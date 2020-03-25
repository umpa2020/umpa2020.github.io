package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.INFOUPDATE
import com.umpa2020.tracer.constant.Constants.Companion.RACINGFINISH
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.trace.decorate.BasicMap
import com.umpa2020.tracer.trace.decorate.PolylineDecorator
import com.umpa2020.tracer.trace.decorate.RacingDecorator
import com.umpa2020.tracer.trace.decorate.TraceMap
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyHandler
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.drawer
import kotlinx.android.synthetic.main.activity_running.*

class RankingRecodeRacingActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
  OnDrawerCloseListener {
  var TAG = "what u wanna say?~~!~!"       //로그용 태그
  lateinit var traceMap: TraceMap
  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapTitle: String
  lateinit var increaseExecuteThread: Thread
  lateinit var chronometer: Chronometer
  var timeWhenStopped: Long = 0

  private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    setContentView(R.layout.activity_ranking_recode_racing)

    mapRouteGPX = intent.getParcelableExtra("RouteGPX") as RouteGPX
    mapTitle = mapRouteGPX.text
    init()
    locationBroadcastReceiver = LocationBroadcastReceiver(traceMap)
    racingControlButton.setOnLongClickListener {
      if (traceMap.userState == UserState.RUNNING) {
        stop(true)
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
        .addOnSuccessListener { Log.d("ssmm11", "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w("ssmm11", "Error updating document", e) }
    })

    increaseExecuteThread.start()
  }

  var mHandler = Handler(App.instance.mainLooper) {
    when (it.what) {
      INFOUPDATE -> {
        racingDistanceTextView.text = traceMap.distance.toString()
        racingSpeedTextView.text = traceMap.speed.toString()
        true
      }
      RACINGFINISH -> {
        stop(true)
        true
      }
      else -> {
        true
      }
    }
  }

  private fun init() {
    MyHandler.myHandler = mHandler
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    traceMap = RacingDecorator(PolylineDecorator(BasicMap(smf, this)))
    traceMap.routeGPX = mapRouteGPX
    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)
    chronometer = racingTimerTextView
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.racingControlButton -> {
        when (traceMap.userState) {
          //TODO:notice " you should be in 200m"
          UserState.NORMAL -> {
            //200m 안으로 들어오세요!
            Log.d(TAG, "NORMAL")
          }
          UserState.READYTORACING -> {
            Log.d(TAG, "READYTORACING")
            start()
          }
          UserState.RUNNING -> {
            Log.d(TAG, "RUNNING")
            stop(false)
          }
        }
      }
      R.id.racingNotificationButton -> {
        racingNotificationLayout.visibility = View.GONE
      }
    }
  }

  fun start() {
    increaseExecute(mapTitle)
    racingNotificationLayout.visibility = View.GONE
    racingControlButton.text = "Stop"
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.start()
    traceMap.start()
  }

  fun stop(result: Boolean) {
    traceMap.stop()

    timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
    chronometer.stop()

    var infoData = InfoData()
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.mapTitle = mapTitle
    //  infoData.distance = calcLeftDistance()

    val newIntent = Intent(this, RacingFinishActivity::class.java)
    newIntent.putExtra("Result", result)
    newIntent.putExtra("InfoData", infoData)
    newIntent.putExtra("MapRouteGPX", mapRouteGPX)

    startActivity(newIntent)
    finish()
  }

  override fun onScrollStarted() {
    Log.d(TAG, "onScrollStarted()")
  }

  override fun onScrollEnded() {
    Log.d(TAG, "onScrollEnded()")
  }

  override fun onDrawerOpened() {
    racingHandle.text = "▼"
    Log.d(TAG, "onDrawerOpened()")
  }

  override fun onDrawerClosed() {
    racingHandle.text = "▲"
    Log.d(TAG, "onDrawerClosed()")
  }
}
