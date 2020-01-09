package com.korea50k.RunShare.Activities.Racing

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.Util.map.RacingMap
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.Util.TTS
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.running_distance_tv
import kotlinx.android.synthetic.main.activity_running.timer_tv

class ManageRacing {
     var racingMap: RacingMap
     var context: Context
    lateinit var chronometer: Chronometer
    lateinit var distanceThread: Thread
     var activity: RacingActivity
     var makerData: RunningData
    var timeWhenStopped: Long = 0
    constructor(
        smf: SupportMapFragment,
        activity: RacingActivity,
        context: Context,
        makerData: RunningData
    ) {
        this.activity = activity
        this.context = context
        this.makerData = makerData
        this.racingMap = RacingMap(smf, context, makerData, this)
    }
    fun startRacing(){
        racingMap.startRacing()
    }
    fun startRunning() {
        distanceThread = Thread(Runnable {
            while (true) {
                Thread.sleep(3000)
                activity.runOnUiThread(Runnable {
                    activity.running_distance_tv.text =
                        String.format("%.3f", (racingMap.getDistance(racingMap.latlngs) / 1000))
                })
            }
        })
        chronometer = activity.timer_tv

       var countDownThread = Thread(Runnable {
           activity.runOnUiThread(Runnable {
               activity.countDownTextView.visibility= View.VISIBLE
           })
           TTS.speech("셋")
            Thread.sleep(1000)
          // TTS.speech("둘")
            activity.runOnUiThread(Runnable {
                activity.countDownTextView.text = "2"
            })

            Thread.sleep(1000)
            TTS.speech("하나")
            activity.runOnUiThread(Runnable {
                activity.countDownTextView.text = "1"
            })
           Thread.sleep(1000)
           TTS.speech("경기를 시작합니다")
           activity.runOnUiThread(Runnable {
               activity.countDownTextView.visibility=View.GONE
           })
           distanceThread.start()
           chronometer.base = SystemClock.elapsedRealtime()
           chronometer.start()
           racingMap.startTracking()
        })

        countDownThread.start()
    }

    fun stopRacing(result:Boolean) {
        racingMap.stopTracking()

        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        var runningData = RunningData()
        runningData.time = chronometer.text.toString()
        if(result){
            //TODO: 여기서 서버로 경기결과 보내기(기록)
        }else{
            //실패하면 안보내도될듯?
        }

        var newIntent = Intent(context, RacingFinishActivity::class.java)
        newIntent.putExtra("Result",result)
        newIntent.putExtra("Racer Data", runningData)
        newIntent.putExtra("Maker Data", makerData)
        context.startActivity(newIntent)
    }
    fun deviation(countDeviation:Int){
        if (countDeviation<=10) {
            activity.noticeMessage("경로를 10초이상 이탈하면 경기가 자동 종료됩니다." + countDeviation + "초")
        }else{
            stopRacing(false)
        }
    }
}