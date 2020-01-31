package com.korea50k.tracer.start

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_running.*
import android.content.Intent
import com.korea50k.tracer.dataClass.*
import com.korea50k.tracer.map.RunningMap
import java.text.SimpleDateFormat
import java.util.*


class ManageRunning {
    lateinit var map: RunningMap
    lateinit var context:Context
    lateinit var activity:RunningActivity
    lateinit var chronometer: Chronometer
    lateinit var distanceThread : Thread
    var timeWhenStopped:Long=0
    var privacy= Privacy.RACING
    constructor(smf: SupportMapFragment, context: Context) {
        this.context=context
        activity=context as RunningActivity
        map = RunningMap(smf, context)
    }

    fun startRunning(activity:RunningActivity) {
        map.startTracking()
        distanceThread = Thread(Runnable {
            while(true) {
                Thread.sleep(1000)
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

    /*fun stopRunning() {
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        var runningData = RunningData()

        //TODO: 이거 RX 기반인거 같아서 주석
        //activity.runningHandle.clicks()
        map.stopTracking(runningData)

        runningData.distance = map.distance
        runningData.time = SystemClock.elapsedRealtime() - chronometer.base
        runningData.privacy=privacy

        var newIntent = Intent((context as Activity), RunningSaveActivity::class.java)
        newIntent.putExtra("Running Data", runningData)
        context.startActivity(newIntent)
        activity.finish()
    }*/

    fun stopRunning() {
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        var routeData = RouteData()
        var infoData = InfoData()

        //TODO: 이거 RX 기반인거 같아서 주석
        //activity.runningHandle.clicks()
        map.stopTracking(routeData, infoData)

        infoData.distance = map.distance
        infoData.time = SystemClock.elapsedRealtime() - chronometer.base
        infoData.privacy=privacy

        var newIntent = Intent((context as Activity), RunningSaveActivity::class.java)
        newIntent.putExtra("Route Data", routeData)
        newIntent.putExtra("Info Data", infoData)
        context.startActivity(newIntent)
        activity.finish()
    }
}