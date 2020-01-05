package com.korea50k.RunShare.Activities.Racing

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.korea50k.RunShare.dataClass.Map
import com.korea50k.RunShare.dataClass.RunningData
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
        this.map = Map(smf, mContext,makerData,this)
    }

    fun startRunning(activity: RacingActivity) {
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

    fun stopRunning() {
        map.pauseTracking()

        timeWhenStopped=chronometer.base-SystemClock.elapsedRealtime()
        chronometer.stop()
        var runningData = RunningData()

        runningData.time = chronometer.text.toString()
        runningData.speed = "0" //TODO : Calc Speed

        var newIntent = Intent(mContext, RacingFinishActivity::class.java)
        newIntent.putExtra("Running Data", runningData)
        newIntent.putExtra("Maker Data",makerData)
        mContext.startActivity(newIntent)
    }
}