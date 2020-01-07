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
import android.view.View
import com.korea50k.RunShare.Activities.Racing.ManageRacing
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.dataClass.TTS
import com.korea50k.RunShare.dataClass.UserState
import kotlinx.android.synthetic.main.activity_racing.*
import kotlin.math.roundToLong


class RacingMap : OnMapReadyCallback {
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
    var countDeviation = 0
    lateinit var currentMarker: Marker
    lateinit var makerMarker: Marker
    lateinit var racerIcon: BitmapDescriptor
    lateinit var makerData: RunningData
    lateinit var manageRacing: ManageRacing
    lateinit var makerRunningThread: Thread
    //Racing
    constructor(
        smf: SupportMapFragment,
        context: Context,
        makerData: RunningData,
        manageRacing: ManageRacing
    ) {
        this.context = context
        smf.getMapAsync(this)
        this.makerData = makerData
        this.manageRacing = manageRacing
        this.load_route = loadRoute()
        userState = UserState.BEFORERACING
        print_log("Set UserState BEFORERACING")

    }

    fun loadRoute(): ArrayList<LatLng> {
        var load_latlngs = ArrayList<LatLng>()
        for (index in makerData.lats.indices) {
            load_latlngs.add(LatLng(makerData.lats[index], makerData.lngs[index]))
        }
        return load_latlngs
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (load_route.size > 0)
            drawRoute(load_route)
        else
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
        initLocation()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        if (userState == UserState.BEFORERACING) {
            val startMarkerOptions = MarkerOptions()
            startMarkerOptions.position(load_route[0])
            startMarkerOptions.title("Maker")
            startMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point))
            mMap.addMarker(startMarkerOptions)

            val finishMarkerOptions = MarkerOptions()
            finishMarkerOptions.position(load_route[load_route.size - 1])
            finishMarkerOptions.title("Maker")
            finishMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_point))
            mMap.addMarker(finishMarkerOptions)
            (context as Activity).runOnUiThread(Runnable {
                TTS.speech("시작 포인트로 이동하세요")
            })
        }

    }

    fun makerRunning() {
        val circleDrawable = context.getDrawable(R.drawable.ic_maker_marker)
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
        var makerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);

        val markerOptions = MarkerOptions()
        markerOptions.position(load_route[0])
        markerOptions.title("Maker")
        markerOptions.icon(makerIcon)
        makerMarker = mMap.addMarker(markerOptions)

        makerRunningThread = Thread(Runnable {
            var time = makerData.time.toString()
            var sec =
                (Integer.parseInt(time[0].toString()) * 10 + Integer.parseInt(time[1].toString())) * 60 + (Integer.parseInt(
                    time[3].toString()
                ) * 10 + Integer.parseInt(time[4].toString()))
            var milisec = ((sec.toDouble() / load_route.size.toDouble()) * 1000).roundToLong()
            print_log(sec.toString() + milisec.toString())

            for (index in load_route.indices) {
                Thread.sleep(milisec)
                (context as Activity).runOnUiThread(Runnable {
                    makerMarker.remove()
                    val markerOptions = MarkerOptions()
                    markerOptions.position(load_route[index])
                    markerOptions.title("Maker")
                    markerOptions.icon(makerIcon)

                    makerMarker = mMap.addMarker(markerOptions)
                })
            }
            (context as Activity).runOnUiThread(Runnable {
                TTS.speech("맵 제작자가 도착했습니다.")
                (context as Activity).countDownTextView.text = "Maker arrive at finish point"
                (context as Activity).countDownTextView.visibility = View.VISIBLE
                print_log("maker arrive")
            })
            Thread.sleep(1500)
            (context as Activity).runOnUiThread(Runnable {
                (context as Activity).countDownTextView.visibility = View.GONE
            })
        })

    }

    fun startTracking() {
        makerRunningThread.start()
    }

    fun stopTracking() {
        print_log("Stop")
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
                    circleDrawable.draw(canvas);
                    racerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);

                    markerOptions.icon(racerIcon)
                    currentMarker = mMap.addMarker(markerOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
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
                        when (userState) {
                            UserState.BEFORERACING -> {
                                (context as Activity).runOnUiThread(Runnable {
                                    (context as Activity).notificationButton.text =("시작 포인트로 이동하십시오.\n시작포인트까지 남은거리 : "
                                    + (SphericalUtil.computeDistanceBetween(cur_loc, load_route[0])).roundToLong().toString() + "m")
                                })
                                if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        load_route[0]
                                    ) < 10
                                ) {
                                    userState=UserState.READYTORACING
                                    (context as Activity).runOnUiThread(Runnable {
                                        (context as Activity).notificationButton.text = "시작"
                                        (context as Activity).notificationButton.isClickable = true
                                        (context as Activity).notificationButton.setOnClickListener {
                                            print_log("Start Racing")
                                            makerRunning()
                                            userState = UserState.RACING
                                            manageRacing.startRunning()
                                            (context as Activity).notificationButton.visibility=View.GONE
                                        }
                                    })
                                }
                            }
                            UserState.READYTORACING->{
                                //wait to start
                            }
                            UserState.RACING -> {
                                print_log("R U Here?" + userState)
                                if (prev_loc.latitude == cur_loc.latitude && prev_loc.longitude == cur_loc.longitude) {
                                    return  //움직임이 없다면 추가안함
                                } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
                                } else if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        load_route[load_route.size - 1]
                                    ) < 10
                                ) {
                                    manageRacing.stopRunning()
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
                            }
                        }

                        prev_loc = cur_loc                              //현재위치를 이전위치로 변경

                        currentMarker.remove()
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

                        when (userState) {
                            UserState.RACING -> {
                                if (PolyUtil.isLocationOnPath(
                                        LatLng(lat, lng),
                                        load_route,
                                        false,
                                        10.0
                                    )
                                ) {
                                    print_log("위도 : " + lat.toString() + "경도 : " + lng.toString())
                                    countDeviation = 0
                                } else {
                                    countDeviation++
                                    print_log("경로이탈")
                                    Toast.makeText(
                                        context,
                                        "경로를 이탈하셨습니다" + countDeviation,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (countDeviation > 10) {
                                        //finish
                                    }

                                }
                            }
                        }
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