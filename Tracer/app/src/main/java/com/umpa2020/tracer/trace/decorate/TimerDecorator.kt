package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import android.widget.Chronometer
import com.umpa2020.tracer.constant.UserState

class TimerDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    lateinit var chronometer: Chronometer
    var timeWhenStopped: Long = 0

    override fun work(location: Location) {
        super.work(location)
        when(userState){
            UserState.RUNNING->{
                calcTime()
            }
        }
    }

    private fun calcTime() {

        time+=1.0
    }
}