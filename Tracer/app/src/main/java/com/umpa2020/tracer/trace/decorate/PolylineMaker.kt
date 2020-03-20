package com.umpa2020.tracer.trace.decorate

import android.location.Location

class PolylineMaker(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun start() {
        super.start()
        startFlag=true
    }
    var startFlag=false
    override fun display(location: Location) {
        super.display(location)
        if(startFlag) polyLineMake()
    }

    private fun polyLineMake() {
        //폴리라인을 그려준다
    }
}