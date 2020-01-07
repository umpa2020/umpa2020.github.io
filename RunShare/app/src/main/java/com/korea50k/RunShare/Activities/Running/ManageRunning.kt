package com.korea50k.RunShare.Activities.Running

import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.map.RunningMap
import kotlinx.android.synthetic.main.activity_running.*
import android.widget.Toast
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.korea50k.RunShare.Activities.Racing.RacingActivity
import com.korea50k.RunShare.dataClass.Privacy


class ManageRunning {
    lateinit var map: RunningMap
    lateinit var context:Context
    lateinit var chronometer: Chronometer
    lateinit var distanceThread : Thread
    var timeWhenStopped:Long=0
    var privacy=Privacy.RACING
    constructor(smf: SupportMapFragment, context: Context) {
        this.context=context
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
        val builder = AlertDialog.Builder(context)
        builder.setTitle("일시정지 하시겠습니까?")
        builder.setMessage("일시정지를 하게 되면 경쟁 모드 업로드가 불가합니다.")
        builder.setPositiveButton("예",
            DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(
                    context,
                    "예를 선택했습니다.",
                    Toast.LENGTH_LONG
                ).show()
                map.pauseTracking()
                timeWhenStopped=chronometer.base-SystemClock.elapsedRealtime()
                chronometer.stop()
                (context as RunningActivity).pause()
                privacy=Privacy.PUBLIC
            })
        builder.setNegativeButton("아니오",
            DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(
                    context,
                    "아니오를 선택했습니다.",
                    Toast.LENGTH_LONG
                ).show()
            })

        builder.show()
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
        runningData.privacy=privacy
        map.CaptureMapScreen(runningData)
        return runningData
    }
}