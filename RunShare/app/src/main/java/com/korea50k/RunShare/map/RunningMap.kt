package com.korea50k.RunShare.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.korea50k.RunShare.Activities.Running.RunningSaveActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.korea50k.RunShare.R
import android.graphics.Canvas
import com.korea50k.RunShare.Activities.Racing.ManageRacing
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.dataClass.UserState
import kotlin.math.roundToLong


class RunningMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var cur_loc: LatLng            //현재위치
    var latlngs: Vector<LatLng> = Vector<LatLng>()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var alts = Vector<Double>()
    var load_route = ArrayList<LatLng>()     //로드할 점들의 집합
    lateinit var context: Context
    lateinit var userState: UserState
     var currentMarker: Marker? = null
    lateinit var racerIcon: BitmapDescriptor

    //Running
    constructor(smf: SupportMapFragment, context: Context) {
        this.context = context
        smf.getMapAsync(this)
        initLocation()
        userState = UserState.RUNNING
        print_log("Set UserState Running")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (load_route.size > 0)
            drawRoute(load_route)
        else
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun startTracking() {
    }

    fun stopTracking(): Triple<DoubleArray, DoubleArray, DoubleArray> {
        print_log("Stop")
        var simplipoly = PolyUtil.simplify(latlngs, 10.0) //tolerance 조절해야함
        var lats: DoubleArray = DoubleArray(simplipoly.size)
        var lngs: DoubleArray = DoubleArray(simplipoly.size)
        for (index in simplipoly.indices) {
            lats[index] = simplipoly[index].latitude
            lngs[index] = simplipoly[index].longitude
        }
        return Triple(lats, lngs, alts.toDoubleArray())
    }

    fun pauseTracking() {
        print_log("pause")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        userState = UserState.PAUSED
        print_log("Set UserState PAUSED")
    }

    fun restartTracking() {
        initLocation()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun drawRoute(route: ArrayList<LatLng>) { //로드 된 경로 그리기
        var polyline =
            mMap.addPolyline(
                PolylineOptions()
                    .addAll(route)
                    .color(Color.RED)
                    .startCap(RoundCap())
                    .endCap(RoundCap())
            )        //경로를 그릴 폴리라인 집합
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                route[0],
                17F
            )
        )                   //맵 줌
        print_log(route[0].toString())
        polyline.tag = "A"
    }

    fun initLocation() {            //첫 위치 설정하고, prev_loc 설정
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    print_log("Location is null")
                } else {
                    print_log("Success to get Init Location : " + location.toString())
                    prev_loc = LatLng(location.latitude, location.longitude)
                    if(currentMarker!=null) currentMarker!!.remove()
                    val markerOptions = MarkerOptions()
                    markerOptions.position(prev_loc)
                    markerOptions.title("Me")

                    val circleDrawable = context.getDrawable(R.drawable.racer_marker)
                    var canvas = Canvas();
                    var bitmap = Bitmap.createBitmap(
                        circleDrawable!!.intrinsicWidth,
                        circleDrawable!!.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    );
                    canvas.setBitmap(bitmap);
                    circleDrawable.setBounds(
                        0,
                        0,
                        circleDrawable.getIntrinsicWidth(),
                        circleDrawable.getIntrinsicHeight()
                    );
                    circleDrawable.draw(canvas);
                    racerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);

                    markerOptions.icon(racerIcon)
                    currentMarker = mMap.addMarker(markerOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
                    if (userState == UserState.PAUSED) {
                        startTracking()
                        userState = UserState.RUNNING
                        print_log("Set UserState Running")
                    }
                }
            }
            .addOnFailureListener {
                print_log("Error is " + it.message.toString())
                it.printStackTrace()
            }

        locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        var lat = location.latitude
                        var lng = location.longitude
                        var alt = location.altitude
                        var speed = location.speed
                        cur_loc = LatLng(lat, lng)
                        if (prev_loc.latitude == cur_loc.latitude && prev_loc.longitude == cur_loc.longitude) {
                            return  //움직임이 없다면 추가안함
                        } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
                        } else {
                            latlngs.add(cur_loc)    //위 조건들을 통과하면 점 추가
                            alts.add(alt)
                            mMap.addPolyline(
                                PolylineOptions().add(
                                    prev_loc,
                                    cur_loc
                                )
                            )   //맵에 폴리라인 추가
                        }
                        prev_loc = cur_loc                              //현재위치를 이전위치로 변경

                        currentMarker!!.remove()
                        val markerOptions = MarkerOptions()
                        markerOptions.position(cur_loc)
                        markerOptions.title("Me")
                        markerOptions.icon(racerIcon)
                        currentMarker = mMap.addMarker(markerOptions)

                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                cur_loc,
                                17F
                            )
                        )        //현재위치 따라서 카메라 이동
                    }
                }
            }
        }
    }

    fun getDistance(locations: Vector<LatLng>): Double {  //점들의 집합에서 거리구하기
        var distance = 0.0
        var i = 0
        while (i < locations.size - 1) {
            distance += SphericalUtil.computeDistanceBetween(locations[i], locations[i + 1])
            i++
        }
        return distance
    }

    fun CaptureMapScreen(runningData: RunningData) {
        //TODO: must to change capture way
        lateinit var bitmap: Bitmap
        var callback = SnapshotReadyCallback {
            try {
                var saveFolder = File(context.filesDir, "mapdata") // 저장 경로
                if (!saveFolder.exists()) {       //폴더 없으면 생성
                    saveFolder.mkdir()
                }
                val path = "racingMap" + saveFolder.list().size + ".bmp"        //파일명 생성하는건데 수정필요

                var myfile = File(saveFolder, path)                //로컬에 파일저장
                var out = FileOutputStream(myfile)
                it.compress(Bitmap.CompressFormat.PNG, 90, out)
                runningData.bitmap = myfile.path


                var newIntent = Intent((context as Activity), RunningSaveActivity::class.java)
                newIntent.putExtra("Running Data", runningData)
                context.startActivity(newIntent)

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