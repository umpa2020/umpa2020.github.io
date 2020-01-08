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
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.dataClass.UserState
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ViewerMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var context: Context
    lateinit var runningData:RunningData
    lateinit var loadLatLngs:ArrayList<LatLng>
    //Running
    constructor(smf: SupportMapFragment, context: Context, runningData:RunningData) {
        this.context = context
        this.runningData=runningData
        loadLatLngs=loadRoute()
        smf.getMapAsync(this)
        print_log("Set UserState NORMAL")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        drawRoute(loadLatLngs)
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
    fun loadRoute(): ArrayList<LatLng> {
        var load_latlngs = ArrayList<LatLng>()
        for (index in runningData.lats.indices) {
            load_latlngs.add(LatLng(runningData.lats[index], runningData.lngs[index]))
        }
        return load_latlngs
    }
    fun drawRoute(route: ArrayList<LatLng>) { //로드 된 경로 그리기
        print_log(route.toString())
        var polyline =
            mMap.addPolyline(
                PolylineOptions()
                    .addAll(route)
                    .color(Color.RED)
                    .startCap(RoundCap())
                    .endCap(RoundCap())
            )        //경로를 그릴 폴리라인 집합
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(route[0],17F))
        //TODO:최대 최소로 값 넣어서 해야함
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(route[0],route[route.size-1]),50))
        print_log(route[0].toString())
        polyline.tag = "A"
    }
    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}