package com.umpa2020.tracer.trace.decorate

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

class PolylineDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun work(location: Location) {
        super.work(location)
        when (userState) {
            UserState.RUNNING -> {
                if (moving) polyLineMake()
            }
            else -> {

            }
        }
    }

    var markerCount = -1
    private fun polyLineMake() {
        Logg.d( "making polyline $previousLocation $currentLocation")
        //polyline 그리기
        mMap.addPolyline(
            PolylineOptions().add(
                previousLocation,
                currentLocation
            )
        )
        //tplist에 추가
        trkList.add(
            WayPoint.builder()
                .lat(currentLocation.latitude)
                .lon(currentLocation.longitude)
                .ele(elevation)
                .speed(speed)
                .name("track point")
                .build()
        )
        //100m마다 waypoint 추가
        if (distance.toInt() / Constants.WPINTERVAL >= markerCount) {
            if (distance > 0) markerCount = distance.toInt() / Constants.WPINTERVAL
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
        }
    }
}