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
import com.jakewharton.rxbinding2.view.clicks
import com.korea50k.RunShare.dataClass.Privacy
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_racing.*
import java.text.SimpleDateFormat
import java.util.*


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
                    activity.runningDistanceTextView.text=String.format("%.3f km",(map.distance/1000))
                })
            }
        })
        distanceThread.start()

        chronometer=activity.runningTimerTextView
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

    fun stopRunning() {
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        var runningData = RunningData()
        activity.runningHandle.clicks()
        map.stopTracking(runningData)

        runningData.distance = map.distance
        runningData.time = formatter.format(Date(SystemClock.elapsedRealtime() - chronometer.getBase()))
        runningData.privacy=privacy

        var newIntent = Intent((context as Activity), RunningSaveActivity::class.java)
        newIntent.putExtra("Running Data", runningData)
        context.startActivity(newIntent)
        activity.finish()
    }
}