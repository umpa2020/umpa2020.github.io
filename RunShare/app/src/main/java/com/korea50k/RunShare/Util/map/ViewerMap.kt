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
import com.korea50k.RunShare.Activities.Running.RunningSaveActivity
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.dataClass.UserState
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ViewerMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var context: Context
    var latlngs: Vector<LatLng> = Vector<LatLng>()
    //Running
    constructor(smf: SupportMapFragment, context: Context) {
        this.context = context
        smf.getMapAsync(this)
        print_log("Set UserState NORMAL")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
    }
    fun CaptureMapScreen(runningData: RunningData) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(latlngs[0],latlngs[latlngs.size-1]),50))
        lateinit var bitmap:Bitmap
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
                var cutBitmap = Bitmap.createBitmap(it, 0, it.height / 2 - 150, it.width, 300)
                cutBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                runningData.bitmap = myfile.path



            } catch (e: Exception) {
                print_log(e.toString())
            }
        }

        mMap.snapshot(callback)
    }
    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}