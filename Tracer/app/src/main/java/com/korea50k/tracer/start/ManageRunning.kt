package com.korea50k.tracer.start

//import androidx.work.PeriodicWorkRequest
import android.content.Context
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.tracer.dataClass.Privacy
import com.korea50k.tracer.map.RunningMap


class ManageRunning {
    val WSY = "WSY"

    lateinit var runningMap: RunningMap
    lateinit var context:Context
    lateinit var activity:RunningActivity
    lateinit var chronometer: Chronometer
    lateinit var distanceThread : Thread
    var timeWhenStopped:Long=0
    var privacy= Privacy.RACING

    constructor(smf: SupportMapFragment, context: Context) {
        this.context=context
        activity=context as RunningActivity
        runningMap = RunningMap(smf, context)

    }

    var i = 0
    fun startRunning(activity:RunningActivity) {
        runningMap.startTracking()

//        Log.d(WSY, "거리 : " + runningMap.distance.toString())
//        distanceThread = Thread(Runnable { // 거리 계산해주는 스레드 -> 일시 정지, 정지 해도 스레드 계속 실행됨.
//            try {
//                while(true) {
//                    Thread.sleep(1000)
//                    Log.d(WSY,"???")
//                    Log.d(WSY, "거리 : " + runningMap.distance.toString())
//                    activity.runOnUiThread(Runnable {
//                        activity.runningDistanceTextView.text=String.format("%.3f",(runningMap.distance/1000))
//                        i += 1
//                        Log.d(WSY,  i.toString())
//                    })
//                }
//            } catch (e : InterruptedException){
//                Log.d(WSY,"intertupt() 실행")
//            }
//
//        })
//        distanceThread.start()
//
//        chronometer=activity.runningTimerTextView
//        chronometer.base=SystemClock.elapsedRealtime()
//        chronometer.start()
    }
    fun restartRunning() {
        runningMap.restartTracking()
//
//        chronometer.base=SystemClock.elapsedRealtime()+timeWhenStopped
//        chronometer.start()
      //  distanceThread.start()
    }

    fun pauseRunning() {
        runningMap.pauseTracking()




//        timeWhenStopped=chronometer.base-SystemClock.elapsedRealtime()
//        chronometer.stop()
//        activity.pause()
//        privacy=Privacy.PUBLIC
//
    }

    fun stopRunning() {
//        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
//        var routeData = RouteData()
//        var infoData = InfoData()
//
//        //TODO: 이거 RX 기반인거 같아서 주석
//        //activity.runningHandle.clicks()
//        runningMap.stopTracking(routeData, infoData)
//
//        infoData.distance = runningMap.distance
//        infoData.time = SystemClock.elapsedRealtime() - chronometer.base
//        infoData.privacy=privacy
//
//        var newIntent = Intent((context as Activity), RunningSaveActivity::class.java)
//        newIntent.putExtra("Route Data", routeData)
//        newIntent.putExtra("Info Data", infoData)
//        context.startActivity(newIntent)
//        activity.finish()
//
//        distanceThread.interrupt() // 일시 정지 상태의 스레드에서 InterruptedException 예외를 발생시켜, 예외 처리 코드(catch)에서 실행 대기 상태로 가거나 종료 상태로 갈 수 있도록 한다.

    }
}