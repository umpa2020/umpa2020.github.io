package com.korea50k.RunShare.Activities.Racing


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.internal.Objects
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.UserState
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.*

class RacingActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRacing: ManageRacing
    lateinit var makerData:RunningData
    lateinit var drawer: SlidingDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing)
        makerData = intent.getSerializableExtra("MakerData") as RunningData
        init()
        racingControlButton.setOnLongClickListener {
            if(manageRacing.racingMap.userState==UserState.RACING) {
                manageRacing.stopRacing(false)

            }
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
            R.id.racingControlButton -> {
                when(manageRacing.racingMap.userState){
                    UserState.BEFORERACING->{
                        Toast.makeText(this,"시작 포인트로 이동하세요",Toast.LENGTH_SHORT).show()
                    }
                    UserState.READYTORACING->{
                        manageRacing.startRacing()
                        racingNotificationButton.visibility=View.GONE
                        racingControlButton.text="  Stop"
                        racingControlButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_icon_pressed,0,0,0)
                    }
                    UserState.RACING->{
                        stop()
                    }
                }
            }
            R.id.racingNotificationButton->{
                racingNotificationLayout.visibility=View.GONE
            }
        }
    }

    fun stop() {    //타이머 멈추는거 만들어야함
        Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
    }
    fun noticeMessage(text:String){
        racingNotificationButton.visibility=View.VISIBLE
        racingNotificationButton.text=text
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