package com.umpa2020.tracer.main.trace.racing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RouteData
import com.umpa2020.tracer.dataClass.UserState
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*

class RankingRecodeRacingActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "RankingRecodeRacingActivity"       //로그용 태그
    lateinit var manageRacing: ManageRacing
    lateinit var makerRouteData: RouteData
    lateinit var drawer: SlidingDrawer
    lateinit var mapTitle: String
    lateinit var increaseExecuteThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setContentView(R.layout.activity_ranking_recode_racing)

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

    fun noticeMessage(text: String) {
        if (text == "") {
            racingNotificationLayout.visibility = View.GONE
        } else {
            racingNotificationLayout.visibility = View.VISIBLE
            racingNotificationButton.text = text
        }
    }

    override fun onResume() {
        super.onResume()
        // 브로드 캐스트 등록 - 전역 context로 수정해야함
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(myBroadcastReceiver, IntentFilter("custom-event-name"))
    }

    override fun onPause() {
        super.onPause()
        //        브로드 캐스트 해제 - 전역 context로 수정해야함
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver)
    }

    /**
     *  백그라운드에서 메시지 받는 거
     */

    // 서버에서 보내주는 데이터를 받는 브로드캐스트 - 나중엔 클래스화 요구??
    private val myBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("$TAG receiver", "받는다.")

            // getParcelableExtra<T> : T = Location
            val message = intent?.getParcelableExtra<Location>("message")
            Log.d("$TAG receiver", "Got message : $message")
            val currentLocation = message as Location
            manageRacing.racingMap.setLocation(currentLocation)
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
