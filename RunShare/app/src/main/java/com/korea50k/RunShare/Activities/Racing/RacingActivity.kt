package com.korea50k.RunShare.Activities.Racing


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_running.btn_stop

class RacingActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRacing: ManageRacing
    lateinit var makerData:RunningData
    lateinit var drawer: SlidingDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing)
        makerData = intent.getSerializableExtra("Running data") as RunningData

        init()
        btn_stop.setOnLongClickListener {
            var runningData = manageRacing.stopRunning()


            true
        }
    }

    fun init() {
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        manageRacing = ManageRacing(smf, this,this, makerData)

        drawer = findViewById(R.id.drawer)
        drawer.setOnDrawerScrollListener(this)
        drawer.setOnDrawerOpenListener(this)
        drawer.setOnDrawerCloseListener(this)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_stop -> stop()
        }
    }

    fun stop() {    //타이머 멈추는거 만들어야함
        Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
    }

    override fun onScrollStarted() {
        Log.d(TAG, "onScrollStarted()")
    }

    override fun onScrollEnded() {
        Log.d(TAG, "onScrollEnded()")
    }

    override fun onDrawerOpened() {
        Log.d(TAG, "onDrawerOpened()")
    }

    override fun onDrawerClosed() {
        Log.d(TAG, "onDrawerClosed()")
    }

    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}