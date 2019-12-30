package com.korea50k.RunShare.Activities.Running

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.DataClass.Map
import com.korea50k.RunShare.DataClass.RunningData

class ManageRunning{
    lateinit var map:Map

    constructor(smf: SupportMapFragment, context: Context){
        map=Map(smf,context)
    }

    fun startRunning(){
        map.startTracking()
    }
    fun restartRunning(){
        map.restartTracking()
    }
    fun pauseRunning(){
        map.pauseTracking()
    }
    fun stopRunning(): RunningData {
        var runningData = RunningData()
        var pair=map.stopTracking()

        runningData.lats=pair.first
        runningData.lngs=pair.second
        runningData.distance = map.getDistance(map.arr_latlng)
        runningData.time="00:12:15" //TODO : Make time thread by chronometer
        runningData.cal=100 //TODO : Calc cal
        runningData.speed=5.3 //TODO : Calc Speed

        return runningData
    }
}