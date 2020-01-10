package com.korea50k.RunShare.Util.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.korea50k.RunShare.R
import android.graphics.Canvas
import android.graphics.Color
import com.korea50k.RunShare.Activities.Running.RunningSaveActivity
import com.korea50k.RunShare.Util.Calc
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.dataClass.UserState
import kotlinx.android.synthetic.main.activity_running_save.*
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ViewerMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var context: Context
    var runningData:RunningData
    var loadRoute= ArrayList<Vector<LatLng>>()
    var smf: SupportMapFragment
    //Running
    constructor(smf: SupportMapFragment, context: Context, runningData:RunningData) {
        this.smf=smf
        this.context = context
        this.runningData=runningData
        smf.getMapAsync(this)
        print_log("Set UserState NORMAL")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadRoute=loadRoute()
        drawRoute(loadRoute)
    }
    fun CaptureMapScreen() {
        var callback = GoogleMap.SnapshotReadyCallback {
            try {
                var saveFolder = File(context.filesDir, "mapdata") // 저장 경로
                if (!saveFolder.exists()) {       //폴더 없으면 생성
                    saveFolder.mkdir()
                }
                val path = "racingMap" + saveFolder.list().size + ".bmp"        //파일명 생성하는건데 수정필요
                //비트맵 크기에 맞게 잘라야함
                var myfile = File(saveFolder, path)                //로컬에 파일저장
                var out = FileOutputStream(myfile)
                print_log(it.width.toString() + "높이 " + it.height.toString())
                it.compress(Bitmap.CompressFormat.PNG, 90, out)
                (context as RunningSaveActivity).save(myfile.path)
            } catch (e: Exception) {
                print_log(e.toString())
            }
        }

        mMap.snapshot(callback)
    }
    fun loadRoute(): ArrayList<Vector<LatLng>> {
        var routes=ArrayList<Vector<LatLng>>()
        for (i in runningData.lats.indices) {
            var latlngs=Vector<LatLng>()
            for(j in runningData.lats[i].indices){
                latlngs.add(LatLng(runningData.lats[i][j],runningData.lngs[i][j]))
            }
            routes.add(latlngs)
        }
        return routes
    }
    fun drawRoute(routes: ArrayList<Vector<LatLng>>) { //로드 된 경로 그리기
        for(i in routes.indices) {
            var polyline =
                mMap.addPolyline(
                    PolylineOptions()
                        .addAll(routes[i])
                        .color(Color.RED)
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                )        //경로를 그릴 폴리라인 집합
        }

        var min= LatLng(Calc.minDouble(runningData.lats),Calc.minDouble(runningData.lngs))
        var max = LatLng(Calc.maxDouble(runningData.lats),Calc.maxDouble(runningData.lngs))
        print_log(min.toString()+max.toString())
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(min,max),1080,300,50))
    }
    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}