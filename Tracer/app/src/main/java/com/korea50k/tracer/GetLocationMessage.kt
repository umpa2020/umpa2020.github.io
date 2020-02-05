package com.korea50k.tracer

import android.location.Location
import android.os.Handler
import android.os.Message
import android.util.Log
import com.korea50k.tracer.locationBackground.LocationBackgroundService
import java.text.DateFormat
import java.util.*

class GetLocationMessage : Handler() {

    var location : Location?
        get() {
            return this.location
        }
        set(obj) {
            location = obj
        }

    override fun handleMessage(msg: Message) {
        Log.i(MainActivity.TAG, "handleMessage..." + msg.toString())

        super.handleMessage(msg)
        when (msg.what) {
            LocationBackgroundService.LOCATION_MESSAGE -> {
                val obj = msg.obj as Location
                val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                location!!.set(obj)
               // toast("LAT :  " + obj.latitude + "\nLNG : " + obj.longitude + "\n\n" + obj.toString() + " \n\n\nLast updated- " + currentDateTimeString)
            }
        }
    }

}