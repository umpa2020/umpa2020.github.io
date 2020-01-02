package com.korea50k.RunShare.Activities.Running


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.korea50k.RunShare.Activities.SaveActivity
import com.korea50k.RunShare.R
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import com.korea50k.RunShare.DataClass.Map
import kotlinx.android.synthetic.main.activity_running.*

class RunningActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener, OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRunning: ManageRunning

    var route_save = ""

    lateinit var drawer: SlidingDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        init()
        manageRunning.startRunning(this)
        btn_stop.setOnLongClickListener {
            /*var route_data = RunningData()
            route_data.route = route_save
            route_data.distance = map.getDistance(map.route)
            route_data.time = timeFormat
            map.CaptureMapScreen(route_data)*/
            var runningData = manageRunning.stopRunning()
            /*var newIntent = Intent(this, SaveActivity::class.java)
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
            R.id.btn_pause -> pause()
            R.id.btn_stop -> stop()
            R.id.btn_restart -> restart()
        }
    }

    /*
    private fun readFile() {                //불러온 파일 읽기
        try {
            // var myfile=File(saveFolder,LoadFileName)            //saveFolder에 LoadFileName 파일
            var myfileRef = mStorageReference.child("maps/" + LoadFileName)
            val ONE_MEGABYTE: Long = 1024 * 1024
            var a = myfileRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener {
                    var buf = it.inputStream().bufferedReader()           //버퍼리더
                    var iterator = buf.lineSequence().iterator()          //버퍼리더의 이터레이터
                    while (iterator.hasNext()) {                                         //한줄 단위로 읽고, 줄이 끝나면 종료
                        var s_line = iterator.next()
                        map.route.add(
                            LatLng(
                                s_line.split(",")[0].toDouble(),
                                s_line.split(",")[1].toDouble()
                            )
                        )      //lat,lng 로 저장되어 있기 때문에 , 기준으로 스플릿해서 추가
                    }
                    buf.close()             //버퍼는 언제나 닫아야함 ㅇㅈ?
                    print_log("Success Read File : " + LoadFileName.toString())
                    map.drawRoute()
                }.addOnFailureListener {
                    print_log("Fail Read File : " + LoadFileName.toString())
                }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
*/
    fun pause() {
        btn_pause.visibility    = View.GONE
        btn_restart.visibility   = View.VISIBLE
        btn_stop.visibility     = View.VISIBLE
        manageRunning.pauseRunning()
    }

    fun stop() {    //타이머 멈추는거 만들어야함
        Toast.makeText(this, "종료를 원하시면, 길게 눌러주세요", Toast.LENGTH_LONG).show()
    }

    fun restart() {
        btn_pause.visibility   = View.VISIBLE
        btn_restart.visibility = View.GONE
        btn_stop.visibility    = View.GONE

        manageRunning.restartRunning()
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