package com.umpa2020.tracer.trace.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import android.graphics.Color
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.main.start.running.RunningSaveActivity
import com.umpa2020.tracer.util.gpx.GPXHelper
import java.io.File
import java.io.FileOutputStream

class ViewerMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var context: Context
    var smf: SupportMapFragment
    var routeGPX: RouteGPX

    //Running
    constructor(smf: SupportMapFragment, context: Context, routeGPX: RouteGPX) {
        this.smf = smf
        this.context = context
        this.routeGPX = routeGPX
        smf.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        drawRoute(GPXHelper().getRoute(routeGPX.trkList))
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
                it.compress(Bitmap.CompressFormat.PNG, 90, out)
                (context as RunningSaveActivity).save(myfile.path)
            } catch (e: Exception) {
                Log.d("Capture",e.toString())
            }
        }

        mMap.snapshot(callback)
    }


    fun drawRoute(track: MutableList<LatLng>) { //로드 된 경로 그리기
        var polyline =
            mMap.addPolyline(
                PolylineOptions()
                    .addAll(track)
                    .color(Color.RED)
                    .startCap(RoundCap() as Cap)
                    .endCap(RoundCap())
            )        //경로를 그릴 폴리라인 집합
        //TODO:MIN MAX
//          mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(track.first(), track.last()), 1080, 300, 50))
    }
}