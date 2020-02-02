package com.korea50k.tracer.map

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
import android.graphics.Canvas
import android.view.View
import com.google.android.gms.maps.*
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.R
import com.korea50k.tracer.TTS
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.NoticeState
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.dataClass.UserState
import com.korea50k.tracer.racing.ManageRacing
import com.korea50k.tracer.racing.RankingRecodeRacingActivity
import com.korea50k.tracer.util.Wow
import com.korea50k.tracer.util.Wow.Companion.makingIcon
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
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
    var latlngs: MutableList<LatLng> = mutableListOf()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var alts = Vector<Double>()
    var speeds = Vector<Double>()
    var loadRoute: MutableList<MutableList<LatLng>> = mutableListOf() //로드된 점들의 집합
    var context: Context
    var userState: UserState
    var countDeviation = 0
    var currentMarker: Marker? = null
    var makerMarker: Marker? = null
    lateinit var racerIcon: BitmapDescriptor
    var makerRouteData: RouteData
    var manageRacing: ManageRacing
    lateinit var makerRunningThread: Thread
    var passedLine = Vector<Polyline>()
    var routeLine = Vector<Polyline>()
    var markers: MutableList<LatLng> = mutableListOf()
    var cpMarkers = Vector<Marker>()
    var db: FirebaseFirestore

    //Racing
    constructor(
        smf: SupportMapFragment,
        context: Context,
        manageRacing: ManageRacing
    ) {
        this.context = context
        this.manageRacing = manageRacing
        this.makerRouteData = manageRacing.makerRouteData
        init()
        smf.getMapAsync(this)
        userState = UserState.BEFORERACING
        print_log("Set UserState BEFORERACING")
        db = FirebaseFirestore.getInstance()

    }

    fun loadRoute() {
        for (i in makerRouteData.latlngs.indices) {
            var latlngs: MutableList<LatLng> = mutableListOf()
            for (j in makerRouteData.latlngs[i].indices) {
                latlngs.add(LatLng(makerRouteData.latlngs[i][j].latitude, makerRouteData.latlngs[i][j].longitude))
            }
            loadRoute.add(latlngs)
            markers.add(LatLng(makerRouteData.markerlatlngs[i].latitude, makerRouteData.markerlatlngs[i].longitude))
        }
        markers.add(
            LatLng(
                makerRouteData.markerlatlngs[makerRouteData.markerlatlngs.size - 1].latitude,
                makerRouteData.markerlatlngs[makerRouteData.markerlatlngs.size - 1].longitude
            )
        )

        loadRoute = makerRouteData.latlngs
        markers = makerRouteData.markerlatlngs
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
        val cpIcon = makingIcon(R.drawable.ic_checkpoint_gray, context)
        //cp 초기화
        cpOption = MarkerOptions()
        cpOption.icon(cpIcon)

    }

    fun makerRunning(mapTitle: String) {
        var makerIcon = makingIcon(R.drawable.ic_maker_marker, context)
        val makerOptions = MarkerOptions()
        makerOptions.position(markers[0])
        makerOptions.title("Maker")
        makerOptions.icon(makerIcon)
        makerMarker = mMap.addMarker(makerOptions)//maker 마커 추가

        makerRunningThread = Thread(Runnable {
            var time = 0.0.toLong()
            db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        time = document.getLong("time")!!
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ssmm11", "Error getting documents.", exception)
                }
            //전체 시간 / 체크포인트 개수 = 한체크포인트에서 머물 시간
            var sleepTime = ((time!!.toDouble() / markers.size.toDouble())).roundToLong()
            print_log("시간 : " + time + ", " + sleepTime.toString())

            for (index in markers.indices) {
                Thread.sleep(sleepTime)
                (context as Activity).runOnUiThread(Runnable {
                    if (makerMarker != null) makerMarker!!.remove() //이전 마커 지우고 새로운 위치로 업데이트
                    val makerOptions = MarkerOptions()
                    makerOptions.position(markers[index])
                    makerOptions.title("Maker")
                    makerOptions.icon(makerIcon)

                    makerMarker = mMap.addMarker(makerOptions)
                })
            }
            (context as Activity).runOnUiThread(Runnable {
                //마커가 끝까지 도착하면
                // TTS.speech("맵 제작자가 도착했습니다.")
                (context as Activity).countDownTextView.text = "Maker arrive at finish point"
                (context as Activity).countDownTextView.visibility = View.VISIBLE
                print_log("maker arrive")
            })
            Thread.sleep(1500)      //1.5초후에 안내 끝
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
            routeLine.add(
                mMap.addPolyline(
                    PolylineOptions()
                        .addAll(loadRoute[i])
                        .color(Color.GRAY)
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                )        //경로를 그릴 폴리라인 집합
            )
            if (i == 0) {   //처음엔 스타트포인트
                var spIcon = makingIcon(R.drawable.ic_racing_startpoint, context)
                val startMarkerOptions = MarkerOptions()
                startMarkerOptions.position(markers[0])
                startMarkerOptions.title("Start")
                startMarkerOptions.icon(spIcon)
                mMap.addMarker(startMarkerOptions)
            } else {        //나머진 다 체크포인트
                cpOption.position(markers[i])
                cpOption.title(i.toString())
                cpMarkers.add(mMap.addMarker(cpOption))
            }
        }
        val cpPassedIcon = makingIcon(R.drawable.ic_checkpoint_red, context)
        cpOption.icon(cpPassedIcon)
        var fpIcon = makingIcon(R.drawable.ic_racing_finishpoint, context)
        val finishMarkerOptions = MarkerOptions()
        finishMarkerOptions.position(markers[markers.size - 1])
        finishMarkerOptions.title("Finish")
        finishMarkerOptions.icon(fpIcon)
        mMap.addMarker(finishMarkerOptions) //마지막엔 피니시 포인트

        var min = LatLng(Wow.minDoubleLat(makerRouteData.latlngs), Wow.minDoubleLng(makerRouteData.latlngs))
        var max = LatLng(Wow.maxDoubleLat(makerRouteData.latlngs), Wow.maxDoubleLng(makerRouteData.latlngs))
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
                    racerIcon = makingIcon(R.drawable.ic_racer_marker, context)

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
                            UserState.BEFORERACING -> { //경기 시작전
                                (context as Activity).runOnUiThread(Runnable {
                                    (context as Activity).racingNotificationButton.text =
                                        ("시작 포인트로 이동하십시오.\n시작포인트까지 남은거리 : "
                                                + (SphericalUtil.computeDistanceBetween(
                                            cur_loc,
                                            markers[0]
                                        )).roundToLong().toString() + "m")
                                })
                                //시작포인트에 10m이내로 들어오면 준비상태로 변경
                                if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        markers[0]
                                    ) <= 10
                                ) {
                                    userState = UserState.READYTORACING
                                }
                            }
                            UserState.READYTORACING -> {
                                //10m밖으로 나가면 상태변경
                                if (SphericalUtil.computeDistanceBetween(
                                        cur_loc,
                                        markers[0]
                                    ) > 10
                                ) {
                                    userState = UserState.BEFORERACING
                                } else {
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
                                        (context as RankingRecodeRacingActivity).racingSpeedTextView.text =
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
                                    //다음 포인트랑 현재 위치랑 10m이내가 되면
                                    if (SphericalUtil.computeDistanceBetween(
                                            cur_loc,
                                            markers[markerCount]
                                        ) < 10
                                    ) {
                                        //이전포인트에서 현재까지 달린 경로 지우고
                                        for (i in passedLine.indices)
                                            passedLine[i].remove()
                                        routeLine[0].remove()
                                        routeLine.removeAt(0)

                                        mMap.addPolyline( //깔끔하게 새로 직선 그리기
                                            PolylineOptions()
                                                .addAll(loadRoute[markerCount - 1])
                                                .color(Color.BLUE)
                                                .startCap(RoundCap())
                                                .endCap(RoundCap())
                                        )        //지나간길

                                        //
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
                                //경로이탈검사
                                if (PolyUtil.isLocationOnPath(
                                        LatLng(lat, lng),
                                        loadRoute[markerCount - 1],
                                        false,
                                        20.0
                                    )
                                ) {//경로 안에 있으면
                                    print_log("위도 : " + lat.toString() + "경도 : " + lng.toString())
                                    if (manageRacing.noticeState == NoticeState.DEVIATION) {
                                        manageRacing.noticeState = NoticeState.NOTHING
                                        countDeviation = 0
                                        manageRacing.deviation(countDeviation)
                                    }
                                } else {//경로 이탈이면
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

    fun startRacing(mapTitle: String) {
        print_log("Start Racing")
        makerRunning(mapTitle)
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