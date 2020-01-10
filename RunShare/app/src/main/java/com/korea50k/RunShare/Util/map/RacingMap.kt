package com.korea50k.RunShare.Util.map

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.korea50k.RunShare.R
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import com.google.android.gms.maps.*
import com.korea50k.RunShare.Activities.Racing.ManageRacing
import com.korea50k.RunShare.Activities.Racing.RacingActivity
import com.korea50k.RunShare.Activities.Running.RunningActivity
import com.korea50k.RunShare.Util.Calc
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.Util.TTS
import com.korea50k.RunShare.dataClass.UserState
import kotlinx.android.synthetic.main.activity_racing.*
import kotlinx.android.synthetic.main.activity_running.*
import kotlin.math.roundToLong


class RacingMap : OnMapReadyCallback {
    var markerCount = 0
    lateinit var cpOption: MarkerOptions
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var cur_loc: LatLng            //현재위치
    var latlngs: Vector<LatLng> = Vector<LatLng>()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var alts = Vector<Double>()
    var speeds = Vector<Double>()
    var loadRoute = ArrayList<Vector<LatLng>>()   //로드된 점들의 집합
    lateinit var context: Context
    lateinit var userState: UserState
    var countDeviation = 0
    var currentMarker: Marker? = null
    var makerMarker: Marker? = null
    lateinit var racerIcon: BitmapDescriptor
    lateinit var makerData: RunningData
    lateinit var manageRacing: ManageRacing
    lateinit var makerRunningThread: Thread
    var passedLine = Vector<Polyline>()
    var markers = Vector<LatLng>()

    //Racing
    constructor(
        smf: SupportMapFragment,
        context: Context,
        manageRacing: ManageRacing
    ) {
        this.context = context
        this.manageRacing = manageRacing
        this.makerData = manageRacing.makerData
        init()
        smf.getMapAsync(this)
        userState = UserState.BEFORERACING
        print_log("Set UserState BEFORERACING")

    }

    fun loadRoute() {
        for (i in makerData.lats.indices) {
            var latlngs = Vector<LatLng>()
            for (j in makerData.lats[i].indices) {
                latlngs.add(LatLng(makerData.lats[i][j], makerData.lngs[i][j]))
            }
            loadRoute.add(latlngs)
            markers.add(LatLng(makerData.markerLats[i], makerData.markerLngs[i]))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadRoute()
        drawRoute()
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                loadRoute[0][0],
                17F
            )
        )

        initLocation()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        if (userState == UserState.BEFORERACING) {
            (context as Activity).runOnUiThread(Runnable {
                TTS.speech("시작 포인트로 이동하세요")
            })
        }
    }

    fun init() {
        val cpMarkerImg = context.getDrawable(R.drawable.checkpoint_marker)
        var cpCanvas = Canvas()
        var cpBitmap = Bitmap.createBitmap(
            cpMarkerImg!!.intrinsicWidth,
            cpMarkerImg!!.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        cpCanvas.setBitmap(cpBitmap);
        cpMarkerImg!!.bounds = Rect(
            0,
            0,
            cpMarkerImg!!.getIntrinsicWidth(),
            cpMarkerImg!!.getIntrinsicHeight()
        )
        cpMarkerImg.draw(cpCanvas)
        val cpIcon = BitmapDescriptorFactory.fromBitmap(cpBitmap);
        //cp 초기화
        cpOption = MarkerOptions()
        cpOption.icon(cpIcon)

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
        markerOptions.position(markers[0])
        markerOptions.title("Maker")
        markerOptions.icon(makerIcon)
        makerMarker = mMap.addMarker(markerOptions)

        makerRunningThread = Thread(Runnable {
            var time = makerData.time.toString()
            var sec =
                (Integer.parseInt(time[0].toString()) * 10 + Integer.parseInt(time[1].toString())) * 60 + (Integer.parseInt(
                    time[3].toString()
                ) * 10 + Integer.parseInt(time[4].toString()))
            var milisec = ((sec.toDouble() / markers.size.toDouble()) * 1000).roundToLong()
            print_log(sec.toString() + milisec.toString())

            for (index in markers.indices) {
                Thread.sleep(milisec)
                (context as Activity).runOnUiThread(Runnable {
                    if (makerMarker != null) makerMarker!!.remove()
                    val markerOptions = MarkerOptions()
                    markerOptions.position(markers[index])
                    markerOptions.title("Maker")
                    markerOptions.icon(makerIcon)

                    makerMarker = mMap.addMarker(markerOptions)
                })
            }
            (context as Activity).runOnUiThread(Runnable {
                // TTS.speech("맵 제작자가 도착했습니다.")
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

    fun drawRoute() { //로드 된 경로 그리기

        for (i in loadRoute.indices) {
            var polyline =
                mMap.addPolyline(
                    PolylineOptions()
                        .addAll(loadRoute[i])
                        .color(Color.RED)
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                )        //경로를 그릴 폴리라인 집합

            if (i == 0) {
                val startMarkerOptions = MarkerOptions()
                startMarkerOptions.position(markers[0])
                startMarkerOptions.title("Start")
                startMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point))
                mMap.addMarker(startMarkerOptions)
            } else if (i == markers.size - 1) {
                val finishMarkerOptions = MarkerOptions()
                finishMarkerOptions.position(markers[markers.size - 1])
                finishMarkerOptions.title("Maker")
                finishMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_point))
                mMap.addMarker(finishMarkerOptions)

            } else {
                cpOption.position(markers[i])
                cpOption.title(i.toString())
                mMap.addMarker(cpOption)
            }
        }

        var min = LatLng(Calc.minDouble(makerData.lats), Calc.minDouble(makerData.lngs))
        var max = LatLng(Calc.maxDouble(makerData.lats), Calc.maxDouble(makerData.lngs))
        print_log(min.toString() + max.toString())
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(min, max), 1080, 300, 50))
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

                    cpOption.title("StartPoint")
                    cpOption.position(prev_loc)
                    mMap.addMarker(cpOption)
                    markerCount++
                    markers.add(prev_loc)
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
                                    (context as Activity).racingNotificationButton.text =
                                        ("시작 포인트로 이동하십시오.\n시작포인트까지 남은거리 : "
                                                + (SphericalUtil.computeDistanceBetween(
                                            cur_loc,
                                            markers[0]
                                        )).roundToLong().toString() + "m")
                                })
                                if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        markers[0]
                                    ) < 10
                                ) {
                                    userState = UserState.READYTORACING
                                    (context as Activity).runOnUiThread(Runnable {
                                        (context as Activity).racingNotificationButton.text =
                                            "시작을 원하시면 START를 누르세요"
                                    })
                                }
                            }
                            UserState.READYTORACING -> {
                                //wait to start
                            }
                            UserState.RACING -> {
                                print_log("R U Here?" + userState)
                                if (prev_loc.latitude == cur_loc.latitude && prev_loc.longitude == cur_loc.longitude) {
                                    return  //움직임이 없다면 추가안함
                                } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
                                } else if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        markers[markers.size - 1]
                                    ) < 10
                                ) {
                                    manageRacing.stopRacing(true)
                                } else {
                                    speeds.add(speed.toDouble())
                                    (context as Activity).runOnUiThread(Runnable {
                                        print_log(speed.toString())
                                        (context as RacingActivity).racingSpeedTextView.text =
                                            String.format("%.3f", speed)
                                    })

                                    latlngs.add(cur_loc)    //위 조건들을 통과하면 점 추가
                                    alts.add(alt)
                                    passedLine.add(
                                        mMap.addPolyline(
                                            PolylineOptions().add(
                                                prev_loc,
                                                cur_loc
                                            )
                                        )
                                    )
                                    if (SphericalUtil.computeDistanceBetween(
                                            cur_loc,
                                            markers[markerCount]
                                        ) < 10
                                    ) {
                                        for (i in passedLine.indices)
                                            passedLine[i].remove()
                                        mMap.addPolyline(
                                            PolylineOptions()
                                                .addAll(loadRoute[markerCount - 1])
                                                .color(Color.BLUE)
                                                .startCap(RoundCap())
                                                .endCap(RoundCap())
                                        )        //지나간길
                                        markerCount++
                                    }
                                }
                            }
                        }

                        prev_loc = cur_loc                              //현재위치를 이전위치로 변경

                        if (currentMarker != null) currentMarker!!.remove()
                        val markerOptions = MarkerOptions()
                        markerOptions.position(cur_loc)
                        markerOptions.title("Me")
                        markerOptions.icon(racerIcon)
                        currentMarker = mMap.addMarker(markerOptions)



                        when (userState) {
                            UserState.RACING -> {
                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        cur_loc,
                                        20F
                                    )
                                )
                                if (PolyUtil.isLocationOnPath(
                                        LatLng(lat, lng),
                                        loadRoute[markerCount - 1],
                                        false,
                                        10.0
                                    )
                                ) {
                                    print_log("위도 : " + lat.toString() + "경도 : " + lng.toString())
                                    countDeviation = 0
                                } else {
                                    manageRacing.deviation(++countDeviation)

                                    if (countDeviation > 10) {
                                        //finish
                                    }

                                }
                            }
                            else -> {
                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        cur_loc,
                                        17F
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun startRacing() {
        print_log("Start Racing")
        makerRunning()
        userState = UserState.RACING
        manageRacing.startRunning()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                cur_loc,
                20F
            )
        )
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