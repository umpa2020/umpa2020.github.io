package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.umpa2020.tracer.constant.UserState

class TimerDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun display(location: Location) {
        super.display(location)
        decoratedMap.testString+="T"
        Log.d(TAG, decoratedMap.testString)
        Log.d(TAG, super.testString)
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