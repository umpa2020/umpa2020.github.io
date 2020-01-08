package com.korea50k.RunShare.Activities.Running


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.Privacy
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.*
import kotlinx.android.synthetic.main.activity_running.btn_stop
import kotlinx.android.synthetic.main.activity_running.handle

class RunningActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener, OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRunning: ManageRunning
    lateinit var drawer: SlidingDrawer
    var B_RUNNIG=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        init()
        manageRunning.startRunning(this)
        btn_stop.setOnLongClickListener {
            var runningData = manageRunning.stopRunning()
            /*var newIntent = Intent(this, RunningSaveActivity::class.java)
            newIntent.putExtra("Running Data",runningData)
            startActivity(newIntent)*/

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

    fun onClick(view: View) {
        when (view.id) {

            R.id.btn_pause -> {
                if(manageRunning.privacy==Privacy.RACING){
                    runningNotificationLayout.visibility= View.VISIBLE
                    runningNotificationTextView.text="일시정지를 하게 되면 경쟁 모드 업로드가 불가합니다.\n일시정지를 원하시면 알림을 클릭하세요"
                }else{
                    if(B_RUNNIG)
                        manageRunning.pauseRunning()
                    else
                        restart()

                }

            }
            R.id.btn_stop -> stop()
            R.id.runningNotificationYes->{
                runningNotificationLayout.visibility=View.GONE
                manageRunning.pauseRunning()
            }
            R.id.runningNotificationNo->{
                runningNotificationLayout.visibility=View.GONE
            }
        }
    }
    fun pause() {
        btn_pause.text="  RESTART"
        btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed,0,0,0)
        B_RUNNIG=false
    }

    fun stop() {    //타이머 멈추는거 만들어야함
        Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
    }

    fun restart() { //TODO:Start with new polyline
        btn_pause.text="  PAUSE"
        btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed,0,0,0)
        B_RUNNIG=true
        manageRunning.restartRunning()
    }

    override fun onScrollStarted() {
        Log.d(TAG, "onScrollStarted()")
    }

    override fun onScrollEnded() {
        Log.d(TAG, "onScrollEnded()")
    }

    override fun onDrawerOpened() {
        handle.background=getDrawable(R.drawable.ic_slidedown_button_unpressed)
        Log.d(TAG, "onDrawerOpened()")
    }

    override fun onDrawerClosed() {
        handle.background=getDrawable(R.drawable.ic_slideup_button_unpressed)
        Log.d(TAG, "onDrawerClosed()")
    }

    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}