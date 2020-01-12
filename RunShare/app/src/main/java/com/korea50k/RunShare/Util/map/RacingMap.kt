package com.korea50k.RunShare.Util.map

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.korea50k.RunShare.R
import android.graphics.Canvas
import android.view.View
import com.google.android.gms.maps.*
import com.korea50k.RunShare.Activities.Racing.ManageRacing
import com.korea50k.RunShare.Activities.Racing.RacingActivity
import com.korea50k.RunShare.Util.Calc
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.Util.TTS
import com.korea50k.RunShare.dataClass.NoticeState
import com.korea50k.RunShare.dataClass.UserState
import kotlinx.android.synthetic.main.activity_racing.*
import kotlin.math.roundToLong


class RacingMap : OnMapReadyCallback {
    var markerCount = 1
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
    var context: Context
    var userState: UserState
    var countDeviation = 0
    var currentMarker: Marker? = null
    var makerMarker: Marker? = null
    lateinit var racerIcon: BitmapDescriptor
    var makerData: RunningData
    var manageRacing: ManageRacing
    lateinit var makerRunningThread: Thread
    var passedLine = Vector<Polyline>()
    var routeLine = Vector<Polyline>()
    var markers = Vector<LatLng>()
    var cpMarkers = Vector<Marker>()

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
        markers.add(
            LatLng(
                makerData.markerLats[makerData.markerLats.size - 1],
                makerData.markerLngs[makerData.markerLngs.size - 1]
            )
        )
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
        val cpIcon = makingIcon(R.drawable.ic_checkpoint_gray)
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

        val makerOptions = MarkerOptions()
        makerOptions.position(markers[0])
        makerOptions.title("Maker")
        makerOptions.icon(makerIcon)
        makerMarker = mMap.addMarker(makerOptions)

        makerRunningThread = Thread(Runnable {
            var time = makerData.time

            var sleepTime = ((time.toDouble() / markers.size.toDouble())).roundToLong()
            print_log("시간 : " + time+", " + sleepTime.toString())

            for (index in markers.indices) {
                Thread.sleep(sleepTime)
                (context as Activity).runOnUiThread(Runnable {
                    if (makerMarker != null) makerMarker!!.remove()
                    val makerOptions = MarkerOptions()
                    makerOptions.position(markers[index])
                    makerOptions.title("Maker")
                    makerOptions.icon(makerIcon)

                    makerMarker = mMap.addMarker(makerOptions)
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

    fun makingIcon(drawable: Int): BitmapDescriptor {
        val circleDrawable = context.getDrawable(drawable)
        var canvas = Canvas()
        var bitmap = Bitmap.createBitmap(
            circleDrawable!!.intrinsicWidth,
            circleDrawable!!.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap);
        circleDrawable.setBounds(
            0,
            0,
            circleDrawable.intrinsicWidth,
            circleDrawable.intrinsicHeight
        )
        circleDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun drawRoute() { //로드 된 경로 그리기

        for (i in loadRoute.indices) {
            routeLine.add(
                mMap.addPolyline(
                    PolylineOptions()
                        .addAll(loadRoute[i])
                        .color(Color.GRAY)
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                )        //경로를 그릴 폴리라인 집합
            )
            if (i == 0) {
                var spIcon = makingIcon(R.drawable.ic_racing_startpoint)
                val startMarkerOptions = MarkerOptions()
                startMarkerOptions.position(markers[0])
                startMarkerOptions.title("Start")
                startMarkerOptions.icon(spIcon)
                mMap.addMarker(startMarkerOptions)
            } else {
                cpOption.position(markers[i])
                cpOption.title(i.toString())
                cpMarkers.add(mMap.addMarker(cpOption))
            }
        }
        val cpPassedIcon = makingIcon(R.drawable.ic_checkpoint_red)
        cpOption.icon(cpPassedIcon)
        var fpIcon = makingIcon(R.drawable.ic_racing_finishpoint)
        val finishMarkerOptions = MarkerOptions()
        finishMarkerOptions.position(markers[markers.size - 1])
        finishMarkerOptions.title("Finish")
        finishMarkerOptions.icon(fpIcon)
        mMap.addMarker(finishMarkerOptions)

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
                    racerIcon = makingIcon(R.drawable.ic_racer_marker)

                    markerOptions.icon(racerIcon)
                    currentMarker = mMap.addMarker(markerOptions)

                    /*cpOption.title("StartPoint")
                    cpOption.position(prev_loc)
                    mMap.addMarker(cpOption)
                    markerCount++
                    markers.add(prev_loc)*/
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
                                    ) <= 10
                                ) {
                                    userState = UserState.READYTORACING
                                }
                            }
                            UserState.READYTORACING -> {
                                if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        markers[0]
                                    ) > 10
                                ) {
                                    userState = UserState.BEFORERACING
                                }else{
                                    (context as Activity).runOnUiThread(Runnable {
                                        (context as Activity).racingNotificationButton.text =
                                            "시작을 원하시면 START를 누르세요"
                                    })
                                }
                            }
                            UserState.RACING -> {
                                print_log("R U Here?" + userState)
                                if (prev_loc.latitude == cur_loc.latitude && prev_loc.longitude == cur_loc.longitude) {
                                    return  //움직임이 없다면 추가안함
                                } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
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
                                            ).color(Color.RED)
                                        )
                                    )
                                    if (SphericalUtil.computeDistanceBetween(
                                            cur_loc,
                                            markers[markerCount]
                                        ) < 10
                                    ) {
                                        for (i in passedLine.indices)
                                            passedLine[i].remove()
                                        routeLine[0].remove()
                                        routeLine.removeAt(0)

                                        mMap.addPolyline(
                                            PolylineOptions()
                                                .addAll(loadRoute[markerCount - 1])
                                                .color(Color.BLUE)
                                                .startCap(RoundCap())
                                                .endCap(RoundCap())
                                        )        //지나간길


                                        if (markerCount == markers.size - 1) {
                                            manageRacing.stopRacing(true)
                                        } else {
                                            cpOption.position(cpMarkers[markerCount - 1].position)
                                            cpMarkers[markerCount - 1].remove()
                                            cpMarkers[markerCount - 1] = mMap.addMarker(cpOption)
                                            markerCount++
                                        }
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
                                        18F
                                    )
                                )
                                if (PolyUtil.isLocationOnPath(
                                        LatLng(lat, lng),
                                        loadRoute[markerCount - 1],
                                        false,
                                        20.0
                                    )
                                ) {
                                    print_log("위도 : " + lat.toString() + "경도 : " + lng.toString())
                                    if(manageRacing.noticeState==NoticeState.DEVIATION) {
                                        manageRacing.noticeState=NoticeState.NOTHING
                                        countDeviation = 0
                                        manageRacing.deviation(countDeviation)
                                    }
                                } else {
                                    manageRacing.deviation(++countDeviation)
                                    if (countDeviation > 30) {
                                        manageRacing.stopRacing(false)
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

    fun calcLeftDistance() {
        var i = markerCount

    }


    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }

}