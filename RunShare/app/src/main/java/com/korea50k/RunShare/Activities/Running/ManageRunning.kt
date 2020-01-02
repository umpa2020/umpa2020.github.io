package com.korea50k.RunShare.Activities.Running

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import androidx.annotation.UiThread
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.DataClass.Map
import com.korea50k.RunShare.DataClass.RunningData
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_running.*
import kotlin.reflect.typeOf

class ManageRunning {
    lateinit var map: Map
    lateinit var mContext:Context
    lateinit var chronometer: Chronometer
    lateinit var distanceThread : Thread
    var timeWhenStopped:Long=0

    constructor(smf: SupportMapFragment, context: Context) {
        mContext=context
        map = Map(smf, mContext)
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

    }

    fun stopRunning(): RunningData {
        var runningData = RunningData()
        var triple = map.stopTracking()

        runningData.lats = triple.first
        runningData.lngs = triple.second
        runningData.alts = triple.third
        runningData.distance = String.format("%.3f",(map.getDistance(map.latlngs)/1000))
        runningData.time = chronometer.text.toString()
        runningData.cal = "0" //TODO : Calc cal
        runningData.speed = "0" //TODO : Calc Speed
        map.CaptureMapScreen(runningData)
        return runningData
    }
}