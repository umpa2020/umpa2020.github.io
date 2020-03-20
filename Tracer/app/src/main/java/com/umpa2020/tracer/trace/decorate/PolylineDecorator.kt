package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.PolylineOptions
import com.umpa2020.tracer.constant.UserState

class PolylineDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun display(location: Location) {
        super.display(location)
        decoratedMap.testString+="P"
        Log.d(TAG, decoratedMap.testString)
        when (userState) {
            UserState.RUNNING -> {
                polyLineMake()
            }
        }
    }

    private fun polyLineMake() {
        Log.d(TAG,"폴리라인")
        mMap.addPolyline(
            PolylineOptions().add(
                previousLocation,
                currentLocation
            )
        )
    }
}