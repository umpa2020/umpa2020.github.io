package com.umpa2020.tracer.trace.decorate

import android.location.Location

abstract class MapDecorator(private val decoratedMap: TraceMap): TraceMap by decoratedMap {
    override fun work(location: Location) {
        decoratedMap.work(location)
    }
}