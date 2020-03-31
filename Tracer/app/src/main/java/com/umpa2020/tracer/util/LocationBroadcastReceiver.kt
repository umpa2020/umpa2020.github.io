package com.umpa2020.tracer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.trace.decorate.TraceMap

/**
 *  브로드 케스트 리시버 클래스
 *  serevice로부터 신호(GPS)를 받아 인자값으로 들어오는 map에 전달.
 */
class LocationBroadcastReceiver(private val map: TraceMap) : BroadcastReceiver() {
    private lateinit var previousLatLng : LatLng
    private var distance : Double? = null
    var flag = true
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getParcelableExtra<Location>("message")
        val currentLocation = message as Location

        if(flag) { // 첫 시작은 값이 들어가야 하므로
            map.work(currentLocation)
            flag = false
        } else {
            val currentLatLng = LatLng(currentLocation.latitude,currentLocation.longitude)
            distance =  SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng)

            if( distance!! < 100 ) // 일단은 100m 이하면 값이 셋팅되게 함.
                map.work(currentLocation)
        }

        previousLatLng = LatLng(currentLocation.latitude,currentLocation.longitude)
    }
}
