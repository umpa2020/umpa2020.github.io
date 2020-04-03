package com.umpa2020.tracer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.umpa2020.tracer.main.MainActivity.Companion.TAG
import com.umpa2020.tracer.trace.decorate.TraceMap

/**
 *  브로드 케스트 리시버 클래스
 *  serevice로부터 신호(GPS)를 받아 인자값으로 들어오는 map에 전달.
 */
class LocationBroadcastReceiver(private val map: TraceMap) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getParcelableExtra<Location>("message")
        var currentLocation = message as Location
        map.work(currentLocation)
    }
}
