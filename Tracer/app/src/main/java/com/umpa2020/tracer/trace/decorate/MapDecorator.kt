package com.umpa2020.tracer.trace.decorate

import android.location.Location
import com.umpa2020.tracer.trace.decorate.TraceMap

abstract class MapDecorator(var decoratedMap: TraceMap): TraceMap() {
    override fun display(location: Location) {
        decoratedMap.display(location)
    }

    override fun start() {
        decoratedMap.start()
    }
}