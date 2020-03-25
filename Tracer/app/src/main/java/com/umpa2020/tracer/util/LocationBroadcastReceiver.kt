package com.umpa2020.tracer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.umpa2020.tracer.main.MainActivity.Companion.TAG
import com.umpa2020.tracer.trace.decorate.TraceMap

class LocationBroadcastReceiver(val map: TraceMap) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("$TAG receiver", "받는다.")
        val message = intent?.getParcelableExtra<Location>("message")
        Log.d("$TAG receiver", "Got message : $message")
        var currentLocation = message as Location
        map.work(currentLocation)
    }
}
