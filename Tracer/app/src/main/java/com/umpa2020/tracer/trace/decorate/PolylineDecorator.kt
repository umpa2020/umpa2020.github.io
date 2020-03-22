package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.util.Wow
import io.jenetics.jpx.WayPoint

class PolylineDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun work(location: Location) {
        super.work(location)
        when (userState) {
            UserState.RUNNING -> {
                if (moving) polyLineMake()
            }
        }
    }

    var markerCount = 0
    private fun polyLineMake() {
        Log.d(TAG, "making polyline $previousLocation $currentLocation")
        mMap.addPolyline(
            PolylineOptions().add(
                previousLocation,
                currentLocation
            )
        )
        tpList.add(
            WayPoint.builder()
                .lat(currentLocation.latitude)
                .lon(currentLocation.longitude)
                .ele(altitude)
                .speed(speed)
                .name("track point")
                .build()
        )
        if (distance.toInt() / Constants.WPINTERVAL >= markerCount) {    //100m마다
            if (distance > 0)
                markerCount = distance.toInt() / Constants.WPINTERVAL
            mMap.addMarker(MarkerOptions().position(currentLocation).title(markerCount.toString()))
            wpList.add(
                WayPoint.builder()
                    .lat(currentLocation.latitude)
                    .lon(currentLocation.longitude)
                    .name("WayPoint")
                    .desc("wayway...")
                    .build()
            )
            markerCount++
            //TODO:tempTPList Simplify
        }
    }
}