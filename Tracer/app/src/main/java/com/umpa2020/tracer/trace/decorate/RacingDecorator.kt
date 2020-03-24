package com.umpa2020.tracer.trace.decorate

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.UserState
import kotlinx.android.synthetic.main.activity_running.view.*
import org.jetbrains.anko.runOnUiThread

class RacingDecorator(decoratedMap: TraceMap) : MapDecorator(decoratedMap) {
    override fun work(location: Location) {
        super.work(location)
        when (userState) {
            UserState.NORMAL -> {
                if(moving) calcDistance()
            }
            UserState.READYTORACING -> {
                if(moving) calcDistance()
            }
            UserState.RUNNING -> {
                if(moving) calcDistance()
            }
        }
    }
    private fun calcDistance() {
        distance += SphericalUtil.computeDistanceBetween(previousLocation, currentLocation)
        /*TODO:context by App instance
        context.runOnUiThread {
            (context as Activity).findViewById<TextView>(R.id.runningDistanceTextView).text=distance.toString()
        }*/
    }
}