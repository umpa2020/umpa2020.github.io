package com.umpa2020.tracer.main.start.racing

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.trace.decorate.BasicMap
import com.umpa2020.tracer.trace.decorate.PolylineDecorator
import com.umpa2020.tracer.trace.decorate.TraceMap
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setContentView(R.layout.activity_ranking_recode_racing)

        mapRouteGPX = intent.getParcelableExtra("RouteGPX") as RouteGPX
        mapTitle = mapRouteGPX.text
        init()
        racingControlButton.setOnLongClickListener {
            if (traceMap.userState == UserState.RUNNING) {
                traceMap.stop()
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

    private fun init() {
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        traceMap = PolylineDecorator(BasicMap(smf, this))
        traceMap.routeGPX=mapRouteGPX
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
                    }
                    UserState.READYTORACING -> {
                        start()
                    }
                    UserState.RUNNING -> {
                        stop()
                    }
                }
            }
            R.id.racingNotificationButton -> {
                racingNotificationLayout.visibility = View.GONE
            }
        }
    }
    fun start(){
        increaseExecute(mapTitle)
        racingNotificationLayout.visibility = View.GONE
        racingControlButton.text = "Stop"
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        traceMap.start()
    }
    fun stop() {    //TODO:타이머 멈추는거 만들어야함
        traceMap.stop()
        SystemClock.elapsedRealtime() - chronometer.base
        // Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
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
