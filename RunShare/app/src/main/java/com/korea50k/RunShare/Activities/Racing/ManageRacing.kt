package com.korea50k.RunShare.Activities.Racing

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.Toast
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.SphericalUtil
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.Calc
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.Util.map.RacingMap
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.Util.TTS
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*

class ManageRacing {
     var racingMap: RacingMap
     var context: Context
    lateinit var chronometer: Chronometer
    lateinit var distanceThread: Thread
     var activity: RacingActivity
     var makerData: RunningData
    var timeWhenStopped: Long = 0
    lateinit var distances:DoubleArray
    constructor(
        smf: SupportMapFragment,
        activity: RacingActivity,
        context: Context,
        makerData: RunningData
    ) {
        this.activity = activity
        this.context = context
        this.makerData = makerData
        this.racingMap = RacingMap(smf, context, this)

    }
    fun startRacing(){
        racingMap.startRacing()
    }
    fun startRunning() {
        distances=DoubleArray(makerData.markerLats.size-1)
        for(i in distances.indices){
            distances[i]=Calc.getDistance(racingMap.loadRoute[i])
        }
        distanceThread = Thread(Runnable {
            while (true) {
                Thread.sleep(3000)
                activity.runOnUiThread(Runnable {
                    activity.racingDistanceTextView.text =
                        String.format("%.3f km", ((calcLeftDistance()) / 1000))
                })
            }
        })
        chronometer = activity.racingTimerTextView

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

    private fun calcLeftDistance() :Double{
        var distance=0.0
        for(i in makerData.markerLats.size-2 downTo racingMap.markerCount)
        {
            distance+=distances[i]
        }
        distance+= SphericalUtil.computeDistanceBetween(racingMap.cur_loc, racingMap.markers[racingMap.markerCount])
        return distance
    }

    fun stopRacing(result:Boolean) {
        racingMap.stopTracking()

        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        var runningData = RunningData()
        runningData.time = formatter.format(Date(SystemClock.elapsedRealtime() - chronometer.getBase()))
        runningData.speed=racingMap.speeds.toDoubleArray()
        Log.d("ssmm11", "맵id = "+makerData.mapTitle )

        if(result){
            Thread(
                Runnable {
                    RetrofitClient.retrofitService.racingResult(
                        //makerData.mapTitle, 널임!
                        makerData.mapTitle,
                        SharedPreValue.getNicknameData(context)!!,
                        runningData.time
                    ).enqueue(object :
                        retrofit2.Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: retrofit2.Response<ResponseBody>
                        ) {
                            try {
                                val resPonseText: String? = response.body().toString()
                                Log.d("response",resPonseText)
                                var newIntent = Intent(context, RacingFinishActivity::class.java)
                                newIntent.putExtra("Result",result)
                                newIntent.putExtra("Racer Data", runningData)
                                newIntent.putExtra("Maker Data", makerData)
                                context.startActivity(newIntent)
                                activity.finish()
                            } catch (e: Exception) {
                                Log.d("response","hmm")
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d("response", t.message);
                            t.printStackTrace()
                        }
                    })
                }).start()

        }else{
            var newIntent = Intent(context, RacingFinishActivity::class.java)
            newIntent.putExtra("Result",result)
            newIntent.putExtra("Racer Data", runningData)
            newIntent.putExtra("Maker Data", makerData)
            context.startActivity(newIntent)
            activity.finish()
        }


    }
    fun deviation(countDeviation:Int){
        if (countDeviation<=10) {
            activity.noticeMessage("경로를 10초이상 이탈하면 경기가 자동 종료됩니다." + countDeviation + "초")
        }else{
            stopRacing(false)
        }
    }
}