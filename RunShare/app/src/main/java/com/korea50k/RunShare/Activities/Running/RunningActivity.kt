package com.korea50k.RunShare.Activities.Running


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.NoticeState
import com.korea50k.RunShare.dataClass.Privacy
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.*
import kotlinx.android.synthetic.main.activity_running.btn_stop
import kotlinx.android.synthetic.main.activity_running.handle

class RunningActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRunning: ManageRunning
    lateinit var drawer: SlidingDrawer
    var B_RUNNIG = true
    var ns = NoticeState.NOTHING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        init()
        manageRunning.startRunning(this)
        btn_stop.setOnLongClickListener {
            if (manageRunning.map.distance < 200) {
                noticeMessage("거리가 200m 미만일때 정지하시면 저장이 불가능합니다. 정지하시겠습니까?", NoticeState.SIOP)
            } else
                manageRunning.stopRunning()
            true
        }
    }

    fun init() {
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        manageRunning = ManageRunning(smf, this)

        drawer = findViewById(R.id.drawer)
        drawer.setOnDrawerScrollListener(this)
        drawer.setOnDrawerOpenListener(this)
        drawer.setOnDrawerCloseListener(this)
    }

    fun noticeMessage(text: String, ns: NoticeState) {
        runningNotificationLayout.visibility = View.VISIBLE
        runningNotificationTextView.text = text
        this.ns = ns

    }

    fun onClick(view: View) {
        when (view.id) {

            R.id.btn_pause -> {
                if (manageRunning.privacy == Privacy.RACING) {
                    noticeMessage("일시정지를 하게 되면 경쟁 모드 업로드가 불가합니다.\n일시정지를 하시겠습니까?", NoticeState.PAUSE)
                } else {
                    if (B_RUNNIG)
                        manageRunning.pauseRunning()
                    else
                        restart()

                }

            }
            R.id.btn_stop -> stop()
            R.id.runningNotificationYes -> {
                when (ns) {
                    NoticeState.NOTHING -> {
                    }
                    NoticeState.PAUSE -> {
                        runningNotificationLayout.visibility = View.GONE
                        manageRunning.pauseRunning()
                    }
                    NoticeState.SIOP -> {
                        var newIntent = Intent(this, MainActivity::class.java)
                        newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(newIntent)
                    }
                }
                ns = NoticeState.NOTHING

            }
            R.id.runningNotificationNo -> {
                ns = NoticeState.NOTHING
                runningNotificationLayout.visibility = View.GONE
            }
        }
    }

    fun pause() {
        btn_pause.text = "  RESTART"
        btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed, 0, 0, 0)
        B_RUNNIG = false
    }

    fun stop() {    //타이머 멈추는거 만들어야함
        Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
    }

    fun restart() { //TODO:Start with new polyline
        btn_pause.text = "  PAUSE"
        btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed, 0, 0, 0)
        B_RUNNIG = true
        manageRunning.restartRunning()
    }

    override fun onScrollStarted() {
        Log.d(TAG, "onScrollStarted()")
    }

    override fun onScrollEnded() {
        Log.d(TAG, "onScrollEnded()")
    }

    override fun onDrawerOpened() {
        handle.background = getDrawable(R.drawable.close_selector)
        Log.d(TAG, "onDrawerOpened()")
    }

    override fun onDrawerClosed() {
        handle.background = getDrawable(R.drawable.extend_selector)
        Log.d(TAG, "onDrawerClosed()")
    }

    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}