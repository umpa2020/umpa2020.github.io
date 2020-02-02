package com.korea50k.tracer.map

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.dataClass.UserState
import com.korea50k.tracer.start.RunningActivity
import com.korea50k.tracer.util.Wow.Companion.makingIcon
import kotlinx.android.synthetic.main.activity_running.*
import java.util.*


class RunningMap (smf: SupportMapFragment, context: Context) : OnMapReadyCallback{
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var TAG = "WSY"       //로그용 태그
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var currentLocation: LatLng            //현재위치
    var latlngs: MutableList<LatLng> = mutableListOf()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var routes: MutableList<MutableList<LatLng>> = mutableListOf()
    var altitude: MutableList<Double> = mutableListOf(.0)
    var speeds: MutableList<Double> = mutableListOf(.0)
    var distance = 0.0
    var context: Context = context
    var userState: UserState
    var markers: MutableList<LatLng> = mutableListOf()
    var markerCount = 0
    var cpOption = MarkerOptions()
    var currentMarker: Marker? = null
    lateinit var racerIcon: BitmapDescriptor

    //Running
    init {
        userState = UserState.RUNNING
        smf.getMapAsync(this)
    }
//    constructor(smf: SupportMapFragment, context: Context){
//        this.context = context
//        userState = UserState.RUNNING
//        smf.getMapAsync(this)
//        print_log("Set UserState Running")
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        init()
        mMap = googleMap
        initLocation()
        //startLocationUpdates()
    }

    private fun init() {
        cpOption
        cpOption.icon(makingIcon(R.drawable.ic_racing_startpoint, context))
    }

    fun startTracking() {
        startLocationUpdates()
    }

    fun stopTracking(routeData: RouteData, infoData: InfoData) {
        stopLocationUpdates() // 위치 업데이트 중지
        print_log("Stop")
        routes.add(PolyUtil.simplify(latlngs, 10.0).toMutableList())
        markers.add(currentLocation)
        var arrLatLng: MutableList<MutableList<LatLng>> = mutableListOf()
        var cpLatLag: MutableList<LatLng> = mutableListOf()
        for(i in routes.indices) {
            var latlngs: MutableList<LatLng> = mutableListOf()
            for (j in routes[i].indices) {
                latlngs.add(LatLng(routes[i][j].latitude,routes[i][j].longitude))
            }
            arrLatLng.add(latlngs)
            cpLatLag.add(LatLng(markers[i].latitude, markers[i].longitude))
        }
        cpLatLag.add(LatLng(markers[markers.size-1].latitude, markers[markers.size-1].longitude))
        routeData.latlngs = arrLatLng
        routeData.markerlatlngs = cpLatLag
        routes.add(PolyUtil.simplify(latlngs, 10.0).toMutableList())

        markers.add(currentLocation)

        routeData.latlngs = routes
        routeData.markerlatlngs = markers
        routeData.altitude = altitude

        //TODO: speed
        infoData.speed = speeds
    }

    fun pauseTracking() {
        print_log("pause")
        stopLocationUpdates() // 위치 업데이트 중지
        userState = UserState.PAUSED
        print_log("Set UserState PAUSED")
    }

    fun restartTracking() {
        initLocation()
        startLocationUpdates()
    }

    /**
     *  위치 요청 설정
     *  정확도: 위치 데이터의 정밀함. 일반적으로 정확도가 높을수록 배터리 소모가 큽니다.
     *  빈도: 위치 연산 빈도. 위치 연산 빈도가 높을수록 배터리를 더 많이 사용합니다.
     *  지연 시간: 위치 데이터의 제공 속도. 일반적으로 지연 시간이 적을수록 더 많은 배터리가 필요합니다.
     *  https://developer.android.com/guide/topics/location/battery?hl=ko#accuracy
     */
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create() // 위치 요청
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 // 위치 받아오는 주기, setinterval() 메서드를 사용하여 앱을 위해 위치를 연산하는 간격을 지정합니다.
            print_log("위치 받아오는 중?")
        }
    }

    /**
     *  위치 업데이트 요청
     *  앱에서 위치 업데이트를 요청하기 전에 위치 서비스에 연결하고 위치를 요청해야 합니다.
     *  위치 설정 변경의 과정에서 이 방법을 보여줍니다. 위치 요청이 완료되면 requestLocationUpdates()를 호출하여 정기 업데이트를 시작할 수 있습니다.
     */
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    /**
     *  마지막으로 알려진 위치 가져오기
     *  위치 서비스 클라이언트를 만든 후 마지막으로 알려진 사용자 기기의 위치를 가져올 수 있습니다
     *  자료 : https://developer.android.com/training/location/retrieve-current?hl=ko#kotlin
     *
     *  마지막 위치를 가져와서 UI 설정. 시작 마커, 현재 자신의 위치 마커
     */
    private fun getLastLocation(fusedLocationClient: FusedLocationProviderClient){
        fusedLocationClient!!.lastLocation // 마지막으로 알려진 위치 가져오기
            .addOnSuccessListener { location ->
                if (location == null) {
                    print_log("Location is null")
                } else {
                    print_log("Success to get Init Location : " + location.toString())
                    previousLocation = LatLng(location.latitude, location.longitude) // 이전 위치
                    if (currentMarker != null) currentMarker!!.remove()
                    val markerOptions = MarkerOptions()
                    markerOptions.position(previousLocation)
                    markerOptions.title("Me")

                    val circleDrawable = context.getDrawable(R.drawable.ic_racer_marker)
                    var canvas = Canvas();
                    var bitmap = Bitmap.createBitmap(
                        circleDrawable!!.intrinsicWidth,
                        circleDrawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
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

                    cpOption.position(previousLocation)
                    mMap.addMarker(cpOption)
                    markerCount++
                    markers.add(previousLocation)
                    cpOption.icon(makingIcon(R.drawable.ic_checkpoint_red, context))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(previousLocation, 17F))
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
    }

    private fun initLocation() {            //첫 위치 설정하고, previousLocation 설정
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context) // 위치 서비스 클라이언트 만들기

        getLastLocation(fusedLocationClient)
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        var lat = location.latitude
                        var lng = location.longitude
                        var alt = location.altitude
                        var speed = location.speed

                        currentLocation = LatLng(lat, lng)
                        if (previousLocation.latitude == currentLocation.latitude && previousLocation.longitude == currentLocation.longitude) {
                            return  //움직임이 없다면 추가안함
                        } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
                        } else {
                            latlngs.add(currentLocation)    //위 조건들을 통과하면 점 추가
                            altitude.add(alt)
                            speeds.add(speed.toDouble())

                            distance += SphericalUtil.computeDistanceBetween(previousLocation, currentLocation)

                            (context as Activity).runOnUiThread(Runnable {
                                print_log("속도 : " + speed.toString())
                                (context as RunningActivity).runningSpeedTextView.text =
                                    String.format("%.3f km/h", speed)
                            })

                            mMap.addPolyline(
                                PolylineOptions().add(
                                    previousLocation,
                                    currentLocation
                                )
                            )   //맵에 폴리라인 추가

                            if (distance.toInt() / 100 >= markerCount) {    //100m마다
                                cpOption.position(currentLocation)
                                markerCount = distance.toInt() / 100
                                cpOption.title(markerCount.toString())
                                mMap.addMarker(cpOption)
                                markers.add(currentLocation)
                                markerCount++
                                routes.add(PolyUtil.simplify(latlngs, 10.0).toMutableList())
                                print_log(routes[routes.size - 1].toString())
                                latlngs.add(currentLocation)
                            }
                        }
                        previousLocation = currentLocation                              //현재위치를 이전위치로 변경

                        if (currentMarker != null) currentMarker!!.remove()
                        val markerOptions = MarkerOptions()
                        markerOptions.position(currentLocation)
                        markerOptions.title("Me")
                        markerOptions.icon(racerIcon)
                        currentMarker = mMap.addMarker(markerOptions)
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
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

    /**
     *  위치 업데이트 중지
     *  사용자가 다른 앱 또는 동일한 앱의 다른 활동으로 전환하는 경우와 같이 더 이상 활동에 포커스가 없을 때 위치 업데이트를 중지
     *  백그라운드에서 실행 중일 때에도 앱이 정보를 수집할 필요가 없는 경우 위치 업데이트를 중지하면 전력 소모를 줄이는 데 도움이 될 수 있습니다.
     */
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }

}