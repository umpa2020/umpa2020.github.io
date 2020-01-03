package com.korea50k.RunShare.Activities.Racing

import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.korea50k.RunShare.DataClass.Map
import com.korea50k.RunShare.DataClass.RunningData
import kotlinx.android.synthetic.main.activity_running.*

class ManageRacing {
    lateinit var map: Map
    lateinit var mContext:Context
    lateinit var chronometer: Chronometer
    lateinit var distanceThread : Thread

    lateinit var makerData: RunningData
    var timeWhenStopped:Long=0

    constructor(smf: SupportMapFragment, context: Context,makerData: RunningData) {
        this.mContext=context
        this.makerData=makerData
        this.map = Map(smf, mContext,loadRoute())
    }

    fun loadRoute():ArrayList<LatLng>{
        var load_latlngs=ArrayList<LatLng>()
        for(index in makerData.lats.indices){
            load_latlngs.add(LatLng(makerData.lats[index],makerData.lngs[index]))
        }
        return load_latlngs
    }

    fun startRunning(activity: RacingActivity) {
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

    fun stopRunning(): RunningData {
        map.pauseTracking()

        timeWhenStopped=chronometer.base-SystemClock.elapsedRealtime()
        chronometer.stop()
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