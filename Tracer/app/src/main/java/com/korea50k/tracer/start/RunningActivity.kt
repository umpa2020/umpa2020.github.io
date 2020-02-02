package com.korea50k.tracer.start


import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.tracer.MainActivity
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.NoticeState
import com.korea50k.tracer.dataClass.Privacy
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_running.*


class RunningActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRunning: ManageRunning
    lateinit var drawer: SlidingDrawer
    var B_RUNNIG = true
    var ns = NoticeState.NOTHING
    private var doubleBackToExitPressedOnce1 = false

    // 버튼 애니메이션
    private var fabOpen: Animation? = null // Floating Animation Button
    private var startButton: Button? = null
    private var stopButton: Button? = null
    private var pauseButton: Button? = null

    override fun onBackPressed() {
        /*
        if (doubleBackToExitPressedOnce1) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce1 = true
        val li = layoutInflater
        val layout: View =
            li.inflate(R.layout.custom_toast_start, findViewById<ViewGroup>(R.id.custom_toast_layout_start))

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 850)
        toast.view = layout //setting the view of custom toast layout

        toast.show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce1 = false }, 2000)
*/
        //Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show()

        if (doubleBackToExitPressedOnce1) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce1 = true


        val text = "뒤로 버튼을 한번 더 누르면 종료됩니다."
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_running)

        init()

        btn_stop!!.setOnLongClickListener {
            if (manageRunning.runningMap.distance < 200) {
                //noticeMessage("거리가 200m 미만일때\n\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?", NoticeState.SIOP)
                showChoicePopup("거리가 200m 미만일때\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?", NoticeState.SIOP)
            } else
                manageRunning.stopRunning()
            true
        }
    }

    private fun init() {
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        manageRunning = ManageRunning(smf, this)

        drawer = findViewById(R.id.drawer)
        drawer.setOnDrawerScrollListener(this)
        drawer.setOnDrawerOpenListener(this)
        drawer.setOnDrawerCloseListener(this)

        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.running_btn_open) // 애니매이션 초기화

        startButton = findViewById(R.id.btn_start)
        stopButton = findViewById(R.id.btn_stop)
        pauseButton = findViewById(R.id.btn_pause)
    }

    /*
    fun noticeMessage(text: String, ns: NoticeState) {
        runningNotificationLayout.visibility = View.VISIBLE
        runningNotificationTextView.text = text
        this.ns = ns

    }

     */
    private fun anim() {
        btn_start.visibility = View.INVISIBLE
        btn_stop.startAnimation(fabOpen)
        btn_pause.startAnimation(fabOpen)
        btn_stop.isClickable = true
        btn_pause.isClickable = true
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start -> {
                anim()
                manageRunning.startRunning(this)
            }
            R.id.btn_pause -> {
                if (manageRunning.privacy == Privacy.RACING) {
                    //noticeMessage("일시정지를 하게 되면\n\n경쟁 모드 업로드가 불가합니다.\n\n일시정지를 하시겠습니까?", NoticeState.PAUSE)
                    showChoicePopup("일시정지를 하게 되면\n경쟁 모드 업로드가 불가합니다.\n\n일시정지를 하시겠습니까?", NoticeState.PAUSE)
                } else {
                    if (B_RUNNIG)
                        manageRunning.pauseRunning()
                    else
                        restart()

                }

            }
            R.id.btn_stop -> stop()

            /*
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
             */
        }
    }

    fun pause() {
        btn_pause.text = "RESTART"
        //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed, 0, 0, 0)
        B_RUNNIG = false
    }

    private fun stop() {    //타이머 멈추는거 만들어야함
        /*
        val li = layoutInflater
        val layout: View =
            li.inflate(R.layout.custom_toast_stop, findViewById<ViewGroup>(R.id.custom_toast_layout_stop))

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 850)
        toast.view = layout //setting the view of custom toast layout

        toast.show()

         */

        //Toast.makeText(this, "종료를 원하시면 길게 눌러주세요", Toast.LENGTH_SHORT).show()

        val text = "종료를 원하시면 길게 눌러주세요"
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    private fun restart() { //TODO:Start with new polyline
        btn_pause.text = "PAUSE"
        //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed, 0, 0, 0)
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
        //runningHandle.background = getDrawable(R.drawable.close_selector)
        runningHandle.text = "▼"
        Log.d(TAG, "onDrawerOpened()")
    }

    override fun onDrawerClosed() {
        //runningHandle.background = getDrawable(R.drawable.extend_selector)
        runningHandle.text = "▲"
        Log.d(TAG, "onDrawerClosed()")
    }

    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }

    /**
     * 팝업 띄우는 함수
     * */
    private fun showChoicePopup(text: String, ns: NoticeState) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(com.korea50k.tracer.R.layout.running_activity_yesnopopup, null)
        val textView: TextView = view.findViewById(com.korea50k.tracer.R.id.runningActivityPopUpTextView)
        textView.text = text

        val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
            .setTitle("선택해주세요.")
            .create()

        //Yes 버튼 눌렀을 때
        val yesButton = view.findViewById<Button>(com.korea50k.tracer.R.id.runningActivityYesButton)
        yesButton.setOnClickListener {
            when (ns) {
                NoticeState.NOTHING -> {
                }
                NoticeState.PAUSE -> {
                    runningNotificationLayout.visibility = View.GONE
                    manageRunning.pauseRunning()
                }
                NoticeState.SIOP -> {
                    manageRunning.stopRunning()
                    var newIntent = Intent(this, MainActivity::class.java)
                    newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(newIntent)
                }
            }
            this.ns = NoticeState.NOTHING
            alertDialog.dismiss()
        }


        //No 기록용 버튼 눌렀을 때
        val recordButton = view.findViewById<Button>(com.korea50k.tracer.R.id.runningActivityNoButton)
        recordButton.setOnClickListener {
            this.ns = NoticeState.NOTHING
            alertDialog.dismiss()
        }

        alertDialog.setView(view)
        alertDialog.show() //팝업 띄우기

        this.ns = ns
    }
}