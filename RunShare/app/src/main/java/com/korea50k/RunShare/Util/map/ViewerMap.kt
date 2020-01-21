package com.korea50k.RunShare.Util.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import android.graphics.Color
import com.korea50k.RunShare.Activities.Running.RunningSaveActivity
import com.korea50k.RunShare.Util.Wow
import com.korea50k.RunShare.dataClass.RunningData
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
        this.runningData=runningData                                //runningData(맵정보)를 받기
        smf.getMapAsync(this)
        print_log("Set UserState NORMAL")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadRoute=loadRoute()                                       //경로 추출
        drawRoute(loadRoute)                                        //추출된 경로 그리기
    }
    fun CaptureMapScreen() {                                        //맵 캡쳐
        var callback = GoogleMap.SnapshotReadyCallback {    //이미지 저장이 백그라운드에서 돌아가므로 callback에서 다음 처리를 진행해줘야함
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
                it.compress(Bitmap.CompressFormat.PNG, 90, out)         //압축
                (context as RunningSaveActivity).save(myfile.path)             //저장된 경로를 인자로 저장함수 실행
            } catch (e: Exception) {
                print_log(e.toString())
            }
        }

        mMap.snapshot(callback)
    }
    fun loadRoute(): ArrayList<Vector<LatLng>> {            //runningData에서 경로 추출
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
        for(i in routes.indices) {                  //반복문으로
            var polyline =
                mMap.addPolyline(
                    PolylineOptions()
                        .addAll(routes[i])
                        .color(Color.RED)
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                )        //경로를 그릴 폴리라인 집합
        }

        //경로에서 최소 최대 위도 경도를 구해서 그 값들이 모두 표시되는 화면으로 보이게끔 맵 카메라 조절
        var min= LatLng(Wow.minDouble(runningData.lats),Wow.minDouble(runningData.lngs))
        var max = LatLng(Wow.maxDouble(runningData.lats),Wow.maxDouble(runningData.lngs))
        print_log(min.toString()+max.toString())
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(min,max),1080,300,50))
    }
    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}