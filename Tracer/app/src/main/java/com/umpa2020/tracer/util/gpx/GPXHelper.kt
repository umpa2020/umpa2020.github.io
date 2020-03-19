package com.umpa2020.tracer.util.gpx

import com.google.android.gms.maps.model.LatLng
import io.jenetics.jpx.WayPoint

class GPXHelper {
    fun getRoute(trkList: MutableList<WayPoint>): MutableList<LatLng> {

        var track = mutableListOf<LatLng>()
        for (trkPoint in trkList)
            track.add(LatLng(trkPoint.latitude.toDouble(),trkPoint.longitude.toDouble()))
        return track
    }

}