package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.umpa2020.tracer.constant.UserState

class DistanceMeter(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun display(location: Location) {
        super.display(location)
        when(userState){
            UserState.RUNNING->{
                calcDistance()
            }
        }
    }

    var startFlag=false
    private fun calcDistance() {
        distance+=1.0
    }
}