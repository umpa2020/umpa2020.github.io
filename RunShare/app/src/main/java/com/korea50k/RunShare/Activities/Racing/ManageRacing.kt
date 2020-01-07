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
    lateinit var racingMap: RacingMap
    lateinit var context: Context
    lateinit var chronometer: Chronometer
    lateinit var distanceThread: Thread
    lateinit var activity: RacingActivity
    lateinit var makerData: RunningData
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

    fun stopRunning() {
        racingMap.stopTracking()

        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        var runningData = RunningData()

        runningData.time = chronometer.text.toString()
        runningData.speed = "0" //TODO : Calc Speed

        var newIntent = Intent(context, RacingFinishActivity::class.java)
        newIntent.putExtra("Racer Data", runningData)
        newIntent.putExtra("Maker Data", makerData)
        context.startActivity(newIntent)
    }
}