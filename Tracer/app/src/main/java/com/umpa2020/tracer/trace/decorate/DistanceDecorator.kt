package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.umpa2020.tracer.constant.UserState

class DistanceDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun display(location: Location) {
        super.display(location)
        decoratedMap.testString+="D"
        Log.d(TAG, decoratedMap.testString)
        when(userState){
            UserState.RUNNING->{
                calcDistance()
            }
        }
    }

    private fun calcDistance() {
        distance+=1.0
    }
}