package com.korea50k.tracer.map

import android.content.Context
import android.os.Looper
import android.util.Log
import com.bumptech.glide.Glide.init
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.korea50k.tracer.Wow.Companion.makingIcon
import com.korea50k.tracer.dataClass.UserState
import com.korea50k.tracer.R



class BasicMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var cur_loc: LatLng            //현재위치
    var context: Context
    var userState: UserState       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var currentMarker: Marker? =null        //사용자 마커
    lateinit var racerIcon: BitmapDescriptor    //사용자 이미지 아이콘(마커에 들어가는 이미지)

    //Running
    constructor(smf: SupportMapFragment, context: Context) {    //객체 생성자
        this.context = context
        userState = UserState.NORMAL
        print_log("Set UserState NORMAL")
        init()                                                  //맵생성전 초기화
        smf.getMapAsync(this)                                   //맵프레그먼트와 연결

    }

    private fun init() { //before the map is loaded
        //TODO:이거 주석 풀어봐야함 왜 오류나는지 몰라서 잠시
        //racerIcon=makingIcon(R.drawable.ic_racer_marker,context)    //아이콘 생성 WOW 클래스 참고
        initLocation()                                             //위치 받는 쓰레드 초기화 받기
    }

    override fun onMapReady(googleMap: GoogleMap) { //after the map is loaded
        mMap = googleMap                                               //구글맵
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))   //화면이동
        fusedLocationClient.requestLocationUpdates(                     //위치정보 요청
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun pauseTracking() {                                           //위치추적 일시정지
        print_log("pause")
        fusedLocationClient.removeLocationUpdates(locationCallback) //위치정보 요청 삭제 = 중단
        userState = UserState.PAUSED                                //userState 변경
        print_log("Set UserState PAUSED")
    }

    fun restartTracking() {                                         //위치추적 재시작
        fusedLocationClient.requestLocationUpdates(                 //위치정보 요청 시작
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        userState=UserState.NORMAL
    }

    fun initLocation() {            //첫 위치 설정하고, prev_loc 설정
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation        //가기의 마지막위치 가져오기(초기위치설정)
            .addOnSuccessListener { location ->
                if (location == null) {
                    print_log("Location is null")
                } else {
                    print_log("Success to get Init Location : " + location.toString())
                    prev_loc = LatLng(location.latitude, location.longitude)    //초기위치 저장
                    val markerOptions = MarkerOptions()
                    markerOptions.position(prev_loc)
                    markerOptions.title("Me")
                    markerOptions.icon(racerIcon)
                    currentMarker = mMap.addMarker(markerOptions)       //현재위치에 marker를 그림
                }
            }
            .addOnFailureListener {
                //TODO:GPS 상태를 확인하세요!
                print_log("Error is " + it.message.toString())
                it.printStackTrace()
            }

        locationRequest = LocationRequest.create()                  //위치 추적 요청 생성
        locationRequest.run {                       //1초간격, 높은정확도로 요청
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() {        //위치요청 결과가 들어오면 실행되는 코드
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        var lat = location.latitude     //결과로 가져온 location에서 정보추출
                        var lng = location.longitude
                        var alt = location.altitude
                        var speed = location.speed
                        cur_loc = LatLng(lat, lng)             //새로받은 정보로 현재위치 설정
                        print_log("Basic Map Log")
                        if(currentMarker!=null)currentMarker!!.remove()     //이미 그려진 마커가 있으면 지우고 재생성
                        val markerOptions = MarkerOptions()
                        markerOptions.position(cur_loc)
                        markerOptions.title("Me")
                        markerOptions.icon(racerIcon)
                        currentMarker = mMap.addMarker(markerOptions)

                        mMap.moveCamera(
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
    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}