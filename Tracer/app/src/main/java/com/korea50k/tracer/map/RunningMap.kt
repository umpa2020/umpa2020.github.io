package com.korea50k.tracer.map

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import android.graphics.Canvas
import com.korea50k.tracer.Wow.Companion.makingIcon
import com.korea50k.tracer.dataClass.RunningData
import com.korea50k.tracer.dataClass.UserState
import com.korea50k.tracer.R
import com.korea50k.tracer.Start.RunningActivity
import kotlinx.android.synthetic.main.activity_running.*


class RunningMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var cur_loc: LatLng            //현재위치
    var latlngs: Vector<LatLng> = Vector<LatLng>()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var routes=ArrayList<Array<LatLng>>()
    var alts = Vector<Double>()
    var speeds = Vector<Double>()
    var distance = 0.0
    var context: Context
    var userState: UserState
    var markers=Vector<LatLng>()
    var markerCount=0
    var cpOption= MarkerOptions()
    var currentMarker:Marker?=null
    lateinit var racerIcon: BitmapDescriptor

    //Running
    constructor(smf: SupportMapFragment, context: Context) {
        this.context = context
        userState = UserState.RUNNING
        smf.getMapAsync(this)
        print_log("Set UserState Running")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        init()
        mMap = googleMap
        initLocation()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

    }
    fun init(){
        cpOption
        cpOption.icon(makingIcon(R.drawable.ic_racing_startpoint,context))
    }
    fun startTracking() {
    }

    fun stopTracking(runningData: RunningData) {
        print_log("Stop")
        routes.add(PolyUtil.simplify(latlngs, 10.0).toTypedArray())
        markers.add(cur_loc)
        var arrLats=Array<Vector<Double>>(routes.size) { Vector() }
        var arrLngs=Array<Vector<Double>>(routes.size) { Vector() }
        var cpLats=Vector<Double>()
        var cpLngs=Vector<Double>()
        for(i in routes.indices) {
            var lats = Vector<Double>()
            var lngs = Vector<Double>()
            for (j in routes[i].indices) {
                lats.add(routes[i][j].latitude)
                lngs.add(routes[i][j].longitude)
            }
            arrLats[i]=lats
            arrLngs[i]=lngs
            cpLats.add(markers[i].latitude)
            cpLngs.add(markers[i].longitude)
        }
        cpLats.add(markers[markers.size-1].latitude)
        cpLngs.add(markers[markers.size-1].longitude)
        runningData.lats=arrLats
        runningData.lngs=arrLngs
        runningData.alts=alts.toDoubleArray()
        runningData.speed=speeds.toDoubleArray()
        runningData.markerLats=cpLats
        runningData.markerLngs=cpLngs
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

                    val circleDrawable = context.getDrawable(R.drawable.ic_racer_marker)
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
                    circleDrawable.draw(canvas)
                    racerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);

                    markerOptions.icon(racerIcon)
                    currentMarker = mMap.addMarker(markerOptions)

                    //startPoint 추가
                    cpOption.title("StartPoint")

                    cpOption.position(prev_loc)
                    mMap.addMarker(cpOption)
                    markerCount++
                    markers.add(prev_loc)
                    cpOption.icon(makingIcon(R.drawable.ic_checkpoint_red,context))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
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
                            speeds.add(speed.toDouble())
                            distance+=SphericalUtil.computeDistanceBetween(prev_loc, cur_loc)
                            (context as Activity).runOnUiThread(Runnable {
                                print_log(speed.toString())
                                (context as RunningActivity).runningSpeedTextView.text=
                                    String.format("%.3f km/h",speed)
                            })
                            mMap.addPolyline(
                                PolylineOptions().add(
                                    prev_loc,
                                    cur_loc
                                )
                            )   //맵에 폴리라인 추가

                            if(distance.toInt()/100>=markerCount){    //100m마다
                                cpOption.position(cur_loc)
                                markerCount=distance.toInt()/100
                                cpOption.title(markerCount.toString())
                                mMap.addMarker(cpOption)
                                markers.add(cur_loc)
                                markerCount++
                                routes.add(PolyUtil.simplify(latlngs, 10.0).toTypedArray())
                                print_log(routes[routes.size-1].toString())
                                latlngs= Vector()
                                latlngs.add(cur_loc)
                            }
                        }
                        prev_loc = cur_loc                              //현재위치를 이전위치로 변경

                        if(currentMarker!=null)currentMarker!!.remove()
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



    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }

}