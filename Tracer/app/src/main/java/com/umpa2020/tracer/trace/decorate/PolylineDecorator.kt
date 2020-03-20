package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.umpa2020.tracer.constant.UserState

class PolylineDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun display(location: Location) {
        super.display(location)
        when (userState) {
            UserState.RUNNING -> {
                polyLineMake()
            }
        }
    }

    private fun polyLineMake() {
        //폴리라인을 그려준다
    }
}