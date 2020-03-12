package com.umpa2020.tracer.main.trace.racing

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.util.TTS
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.NoticeState
import com.umpa2020.tracer.dataClass.RouteData
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.locationBackground.ServiceStatus
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.main.MainActivity.Companion.MESSENGER_INTENT_KEY
import com.umpa2020.tracer.map.RacingMap
import com.umpa2020.tracer.util.Wow
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
import java.text.DateFormat
import java.util.*

/*

 */


class ManageRacing {
    var racingMap: RacingMap
    var context: Context
    var noticeState = NoticeState.NOTHING
    lateinit var chronometer: Chronometer
    lateinit var distanceThread: Thread
    var activity: RankingRecodeRacingActivity
    var makerRouteData: RouteData
    var timeWhenStopped: Long = 0
    lateinit var distances: DoubleArray
    lateinit var mapTitle: String

    constructor(
        smf: SupportMapFragment,
        activity: RankingRecodeRacingActivity,
        context: Context,
        makerRouteData: RouteData
    ) {
        this.activity = activity
        this.context = context
        this.makerRouteData = makerRouteData
        this.racingMap = RacingMap(smf, context, this)

    }

    fun startRacing(mapTitle: String) {
        this.mapTitle = mapTitle
        racingMap.startRacing(mapTitle)
    }

    fun startRunning() {
        distances = DoubleArray(makerRouteData.markerlatlngs.size - 1)
        for (i in distances.indices) {
            distances[i] = Wow.getDistance(racingMap.loadRoute[i])
        }
        distanceThread = Thread(Runnable {
            while (true) {
                Thread.sleep(1000)
                activity.runOnUiThread(Runnable {
                    activity.racingDistanceTextView.text =
                        String.format("%.2f km", ((calcLeftDistance()) / 1000))
                })
            }
        })
        chronometer = activity.racingTimerTextView

        var countDownThread = Thread(Runnable {
            activity.runOnUiThread(Runnable {
                activity.countDownTextView.visibility = View.VISIBLE
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
                activity.countDownTextView.visibility = View.GONE
            })
            distanceThread.start()
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            racingMap.startTracking()
        })

        countDownThread.start()
    }

    private fun calcLeftDistance(): Double {
        var distance = 0.0
        for (i in makerRouteData.markerlatlngs.size - 2 downTo racingMap.markerCount) {
            distance += distances[i]
        }
        distance += SphericalUtil.computeDistanceBetween(racingMap.currentLocation, racingMap.markers[racingMap.markerCount])
        return distance
    }

    fun stopRacing(result: Boolean) {
        racingMap.stopTracking()

        removeHandler()
        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()

        var infoData = InfoData()
        infoData.time = SystemClock.elapsedRealtime() - chronometer.base
        infoData.speed = racingMap.speeds
        infoData.mapTitle = mapTitle
        infoData.distance = calcLeftDistance()

        if (result) {
            val newIntent = Intent(context, RacingFinishActivity::class.java)
            newIntent.putExtra("Result",    result)
            newIntent.putExtra("info Data", infoData)
            newIntent.putExtra("Maker Data", makerRouteData)
            Log.d("ssmm11", "레이싱 끝!")

            context.startActivity(newIntent)
            activity.finish()

        } else {
            val newIntent = Intent(context, RacingFinishActivity::class.java)
            newIntent.putExtra("Result", result)
            newIntent.putExtra("info Data", infoData)
            newIntent.putExtra("Maker Data", makerRouteData)

            context.startActivity(newIntent)
            activity.finish()
        }
    }

    private fun removeHandler() {
        Log.d(MainActivity.TAG, "이건 실행되잖아?")
        Intent(context.applicationContext, LocationBackgroundService::class.java).also {
            it.action = ServiceStatus.STOP.name
            context.applicationContext.startService(it)
        }
    }

    var mHandler: IncomingMessageHandler? = null
    val MESSENGER_INTENT_KEY = "msg-intent-key"

    inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.d(MainActivity.WSY, "EmptyHandler : $msg")
        }
    }

    fun deviation(countDeviation: Int) {
        if (countDeviation == 0) {
            activity.noticeMessage("")
        } else if (countDeviation <= 20) {
            noticeState = NoticeState.DEVIATION
            activity.noticeMessage("경로를 20초이상 이탈하면\n경기가 자동 종료됩니다.\n" + countDeviation + "초")
        } else {
            stopRacing(false)
        }
    }
}