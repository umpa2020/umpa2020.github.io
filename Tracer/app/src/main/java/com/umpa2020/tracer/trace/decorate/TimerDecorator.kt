package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.umpa2020.tracer.constant.UserState

class TimerDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun display(location: Location) {
        super.display(location)
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