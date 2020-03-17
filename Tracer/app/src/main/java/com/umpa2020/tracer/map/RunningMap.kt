package com.umpa2020.tracer.map

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteData
import com.umpa2020.tracer.dataClass.UserState
import com.umpa2020.tracer.locationBackground.LocationUpdatesComponent
import com.umpa2020.tracer.main.trace.running.RunningActivity
import com.umpa2020.tracer.util.Wow.Companion.makingIcon
import kotlinx.android.synthetic.main.activity_running.*


/**
 *  앱에서 지도를 사용하려면 OnMapReadyCallback 인터페이스를 구현하고
 *  MapFragment 객체에서 getMapAsync(OnMapReadyCallback)를 통해 콜백 인스턴스를 설정해야 한다.
 */
class RunningMap(smf: SupportMapFragment, override var context: Context) : BasicMap(smf,context) {

//    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    var TAG = "WSY"       //로그용 태그

//    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
//    var currentLocation: LatLng = LatLng(0.0, 0.0)              //현재위치

    var latlngs: MutableList<LatLng> = mutableListOf()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var routes: MutableList<MutableList<LatLng>> = mutableListOf()
    var altitude: MutableList<Double> = mutableListOf(.0)
    var speeds: MutableList<Double> = mutableListOf(.0)
    var distance = 0.0
//    var userState: UserState? = null
    var markers: MutableList<LatLng> = mutableListOf()
    var markerCount = 0

//    var cameraFlag = false

    var cpOption = MarkerOptions()

    var currentMarker: Marker? = null

    lateinit var myIcon: BitmapDescriptor

    //Running
    init {
        userState = UserState.PAUSED
        smf.getMapAsync(this)
        //createMyIcon()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.

        // 마지막 위치 가져와서 카메라 설정
        Log.d(TAG, "잘 가져왔니? " + LocationUpdatesComponent.getLastLocat().toString())
        val lat =  LocationUpdatesComponent.getLastLocat().latitude
        val lng =  LocationUpdatesComponent.getLastLocat().longitude
        currentLocation = LatLng(lat, lng)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
    }
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
//
//        // 마지막 위치 가져와서 카메라 설정
//        Log.d(TAG, "잘 가져왔니? " + LocationUpdatesComponent.getLastLocat().toString())
//        val lat =  LocationUpdatesComponent.getLastLocat().latitude
//        val lng =  LocationUpdatesComponent.getLastLocat().longitude
//        currentLocation = LatLng(lat, lng)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
//    }


    fun startTracking() {
       // setStartIcon()  // 마지막 현재 위치에 아이콘 설정
        mMap!!.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        userState = UserState.RUNNING
    }

    fun stopTracking(routeData: RouteData, infoData: InfoData) {
        userState = UserState.PAUSED

        mMap!!.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        print_log("Stop")
        if (latlngs.size > 0) {
            Log.d("ssmm11", "latlngs = " + latlngs.size)
            routes.add(PolyUtil.simplify(latlngs, 10.0).toMutableList())
        }
        markers.add(currentLocation)
        val arrLatLng: MutableList<MutableList<LatLng>> = mutableListOf()
        val cpLatLag: MutableList<LatLng> = mutableListOf()
        for (i in routes.indices) {
            val latlngs: MutableList<LatLng> = mutableListOf()
            for (j in routes[i].indices) {
                latlngs.add(LatLng(routes[i][j].latitude, routes[i][j].longitude))
            }
            arrLatLng.add(latlngs)
            cpLatLag.add(LatLng(markers[i].latitude, markers[i].longitude))
        }
        cpLatLag.add(LatLng(markers[markers.size - 1].latitude, markers[markers.size - 1].longitude))
        routeData.latlngs = arrLatLng
        routeData.markerlatlngs = cpLatLag

        routeData.altitude = altitude

        //TODO: speed
        infoData.speed = speeds
        Log.d("ssmm11", "스피드를 안넣나 = " + infoData.speed)
    }

    fun pauseTracking() {
        print_log("pause")
        //  stopLocationUpdates() // 위치 업데이트 중지
        userState = UserState.PAUSED
        print_log("Set UserState PAUSED")
    }

    fun restartTracking() {
        //  startLocationUpdates()
    }

    /**
     *  내 아이콘 만들기 메소드
     *  위치 관련 정보 넣는거 없이 순수 아이콘 생성.
     */
//    private fun createMyIcon() {
//
//        val circleDrawable = context.getDrawable(R.drawable.ic_racer_marker)
//        var canvas = Canvas()
//        //TODO: 기본 파란색 으로 하는걸로 - 삭제 필요
//        var bitmap = Bitmap.createBitmap(
//            circleDrawable!!.intrinsicWidth,
//            circleDrawable!!.intrinsicHeight,
//            Bitmap.Config.ARGB_8888
//        )
//        canvas.setBitmap(bitmap)
//        circleDrawable.setBounds(
//            0,
//            0,
//            circleDrawable.intrinsicWidth,
//            circleDrawable.intrinsicHeight
//        )
//        circleDrawable.draw(canvas)
//        myIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
//    }

    /**
     *  start Icon 설정
     *  현재 위치의 마지막 지점에 설정.
     */
    private fun setStartIcon() {
        cpOption.title("StartPoint")

        cpOption.position(previousLocation)
        markerCount++
        markers.add(previousLocation)

        cpOption.icon(makingIcon(R.drawable.ic_racing_startpoint, context))
        mMap!!.addMarker(cpOption)
    }

    var location = null

    /**
     *  서비스에서 데이터 받아오는 부분
     */
    override fun setLocation(location: Location) {
        if (userState == UserState.RUNNING)
            createData(location)
        else
            currentLocation = LatLng(location.latitude, location.longitude)

        if(!cameraFlag) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
            cameraFlag = true
        }

//        setMyIconToMap()
        previousLocation = currentLocation                              //현재위치를 이전위치로 변경
    }

    // 이 데이터 받아서 처리
    // Location[fused 37.619672,127.059084 hAcc=15 et=+5d2h34m37s51ms alt=53.5 vel=0.0014348121 bear=219.74748 vAcc=2 sAcc=??? bAcc=??? {Bundle[mParcelledData.dataSize=52]}]

    private fun createData(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val alt = location.altitude
        val speed = location.speed

        currentLocation = LatLng(lat, lng)

        if (previousLocation.latitude == currentLocation.latitude && previousLocation.longitude == currentLocation.longitude) {
            return  //움직임이 없다면 추가안함
        } else if (false) { //비정상적인 움직임일 경우 + finish에 도착한 경우
        } else {
            latlngs.add(currentLocation)    //위 조건들을 통과하면 점 추가
            altitude.add(alt)
            speeds.add(speed.toDouble())

            getDistance()

            // 속도 UI 스레드
            (context as Activity).runOnUiThread(Runnable {
                print_log("속도 : " + speed.toString())
                (context as RunningActivity).runningSpeedTextView.text =
                    String.format("%.2f km/h", speed)
            })

            // 위치가 계속 업데이트 되어야해서 여기에 선언
            createPolyline()  // 이전 위치, 현재 위치로 폴리라인 형성


            /**
             *  100m마다 체크 포인트 찍는거
             */
            if (distance.toInt() / 100 >= markerCount) {    //100m마다
                cpOption.position(currentLocation)
                if (distance > 0)
                    markerCount = distance.toInt() / 100
                cpOption.title(markerCount.toString())
                cpOption.icon(makingIcon(R.drawable.ic_checkpoint_red, context))
                mMap!!.addMarker(cpOption) // 이게 기본 마커 찍나? => start지점 찍으려는거 같은데
                markers.add(currentLocation)
                markerCount++
                routes.add(PolyUtil.simplify(latlngs, 10.0).toMutableList())
                print_log(routes[routes.size - 1].toString())
                latlngs.clear()
                latlngs.add(currentLocation)
            }
        }// if 문 끝
    }

    /**
     *    이전 위치( previousLocation ), 현재 위치( currentLocation )로 폴리라인 추가.
     *    맵에 폴리라인 추가
     *    => 추후 그리는 후 처리 필요
     */
    private fun createPolyline() {
        print_log("폴리라인")
        mMap!!.addPolyline(
            PolylineOptions().add(
                previousLocation,
                currentLocation
            )
        )
    }

    /**
     *   내 위치 마커로 계속 업데이트
     *   현재 위치(currentLocation)
     */
//    private fun setMyIconToMap() {
//        if (currentMarker != null) currentMarker!!.remove() // 이전 마커 지워주는 행동
//
//        val markerOptions = MarkerOptions()
//        markerOptions.position(currentLocation)
//        markerOptions.title("Me")
//        markerOptions.icon(myIcon)
//        currentMarker = mMap.addMarker(markerOptions) // 이게 내 아이콘 찍네 => 지도에 현재 위치 찍는 듯?
//
//        /**
//         *   현재위치 따라서 카메라 이동
//         */
//        //카메라를 이전 위치로 옮긴다.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
////        mMap.animateCamera(
////            CameraUpdateFactory.newLatLngZoom(
////                currentLocation,
////                17F
////            )
////        )
//    }

    fun getDistance() {

        // 이전 위치, 현재 위치로 거리 계산
        distance += SphericalUtil.computeDistanceBetween(previousLocation, currentLocation)
    }
}