package com.umpa2020.tracer.main.trace.racing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RouteData
import com.umpa2020.tracer.dataClass.UserState
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.main.MainActivity
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
import java.text.DateFormat
import java.util.*

class RankingRecodeRacingActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
  OnDrawerCloseListener {
  var TAG = "WSY"       //로그용 태그
  lateinit var manageRacing: ManageRacing
  lateinit var makerRouteData: RouteData
  lateinit var drawer: SlidingDrawer
  lateinit var mapTitle: String
  lateinit var increaseExecuteThread: Thread

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_ranking_recode_racing)

    // 서비스에 레이싱 핸들러 등록
    mHandler = IncomingMessageHandler()
    Log.d(MainActivity.WSY, "핸들러 생성?")
    Intent(this, LocationBackgroundService::class.java).also {
      val messengerIncoming = Messenger(mHandler)
      it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
      startService(it)
    }
    init()
    racingControlButton.setOnLongClickListener {
      if (manageRacing.racingMap.userState == UserState.RACING) {
        manageRacing.stopRacing(false)
      }
      true
    }
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

  fun init() {
    //루트 받아오기
    makerRouteData = intent.getParcelableExtra("makerRouteData") as RouteData
    mapTitle = intent.getStringExtra("maptitle")!!

    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    manageRacing = ManageRacing(smf, this, this, makerRouteData)

    drawer = findViewById(R.id.drawer)
    drawer.setOnDrawerScrollListener(this)
    drawer.setOnDrawerOpenListener(this)
    drawer.setOnDrawerCloseListener(this)
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.racingControlButton -> {
        when (manageRacing.racingMap.userState) {
          UserState.BEFORERACING -> {
          }
          UserState.READYTORACING -> {
            increaseExecute(mapTitle)
            manageRacing.startRacing(mapTitle)
            racingNotificationLayout.visibility = View.GONE
            racingControlButton.text = "Stop"
          }
          UserState.RACING -> {
            stop()
          }
        }
      }
      R.id.racingNotificationButton -> {
        racingNotificationLayout.visibility = View.GONE
      }
    }
  }

  fun stop() {    //타이머 멈추는거 만들어야함
    // Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
  }

  //TODO: text null일때 테스트
  fun noticeMessage(text: String) {
    racingNotificationLayout.visibility = if (text.isBlank()) View.GONE else View.VISIBLE
    if (text.isNotBlank()) {
      racingNotificationButton.text = text
    }
  }

  /**
   *  백그라운드에서 메시지 받는 거
   */
  var mHandler: IncomingMessageHandler? = null
  val MESSENGER_INTENT_KEY = "msg-intent-key"

  inner class IncomingMessageHandler : Handler() {
    override fun handleMessage(msg: Message) {
      Log.i(MainActivity.WSY, "handleMessage..." + msg.toString())
      super.handleMessage(msg)
      when (msg.what) {
        LocationBackgroundService.LOCATION_MESSAGE -> {
          val obj = msg.obj as Location
          val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
          Log.d(MainActivity.WSY, "RacingActivity : 값을 가져옴?")
          manageRacing.racingMap.setLocation(obj)
//                    manageRunning.runningMap.setLocation(obj)
        }
      }
    }
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
