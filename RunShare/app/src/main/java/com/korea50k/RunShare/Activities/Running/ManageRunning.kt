package com.korea50k.RunShare.Activities.Running

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.Util.map.RunningMap
import kotlinx.android.synthetic.main.activity_running.*
import android.widget.Toast
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.korea50k.RunShare.dataClass.Privacy
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.running_distance_tv
import kotlinx.android.synthetic.main.activity_running.timer_tv


class ManageRunning {
    lateinit var map: RunningMap
    lateinit var context:Context
    lateinit var activity:RunningActivity
    lateinit var chronometer: Chronometer
    lateinit var distanceThread : Thread
    var timeWhenStopped:Long=0
    var privacy=Privacy.RACING
    constructor(smf: SupportMapFragment, context: Context) {
        this.context=context
        activity=context as RunningActivity
        map = RunningMap(smf, context)
    }

    fun startRunning(activity:RunningActivity) {
        map.startTracking()
        distanceThread = Thread(Runnable {
            while(true) {
                Thread.sleep(3000)
                activity.runOnUiThread(Runnable {
                    activity.running_distance_tv.text=String.format("%.3f",(map.getDistance(map.latlngs)/1000))
                })
            }
        })
        distanceThread.start()

        chronometer=activity.timer_tv
        chronometer.base=SystemClock.elapsedRealtime()
        chronometer.start()
    }

    fun restartRunning() {
        map.restartTracking()

        chronometer.base=SystemClock.elapsedRealtime()+timeWhenStopped
        chronometer.start()
    }
    fun pauseRunning() {
        map.pauseTracking()
        timeWhenStopped=chronometer.base-SystemClock.elapsedRealtime()
        chronometer.stop()
        activity.pause()
        privacy=Privacy.PUBLIC
    }

    fun stopRunning(): RunningData {
        var runningData = RunningData()
        map.stopTracking(runningData)

        runningData.distance = String.format("%.3f",(map.getDistance(map.latlngs)/1000))
        runningData.time = chronometer.text.toString()
        runningData.cal = "0" //TODO : Calc cal
        runningData.privacy=privacy

        var newIntent = Intent((context as Activity), RunningSaveActivity::class.java)
        newIntent.putExtra("Running Data", runningData)
        context.startActivity(newIntent)

        return runningData
    }
}