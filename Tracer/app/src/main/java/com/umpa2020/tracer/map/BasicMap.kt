package com.umpa2020.tracer.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.UserState

class BasicMap : OnMapReadyCallback {
    var mMap: GoogleMap? = null    //racingMap 인스턴스

    var TAG = "BasicMap"       //로그용 태그
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    var currentLocation: LatLng = LatLng(0.0, 0.0)              //현재위치
    var context: Context
    var userState: UserState       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var currentMarker: Marker? = null        //사용자 마커
    lateinit var myIcon: BitmapDescriptor    //사용자 이미지 아이콘(마커에 들어가는 이미지)

    //Running
    constructor(smf: SupportMapFragment, context: Context) {    //객체 생성자
        this.context = context
        userState = UserState.NORMAL
        smf.getMapAsync(this)                                   //맵프레그먼트와 연결
        createMyIcon()
    }

    override fun onMapReady(googleMap: GoogleMap) { //after the map is loaded
        Log.d("ssmm11", "onMapReady")

        mMap = googleMap //구글맵
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(previousLocation, 17F))   //화면이동
    }

    fun setLocation(location: Location) {
        var lat = location!!.latitude
        var lng = location!!.longitude
        currentLocation = LatLng(lat, lng)

        setMyIconToMap()

        previousLocation = currentLocation                              //현재위치를 이전위치로 변경
    }

    /**
     *  내 아이콘 만들기 메소드
     *  위치 관련 정보 넣는거 없이 순수 아이콘 생성.
     */
    private fun createMyIcon() {

        val circleDrawable = context.getDrawable(R.drawable.ic_racer_marker)
        var canvas = Canvas()
        var bitmap = Bitmap.createBitmap(
            circleDrawable!!.intrinsicWidth,
            circleDrawable!!.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        circleDrawable.setBounds(
            0,
            0,
            circleDrawable.intrinsicWidth,
            circleDrawable.intrinsicHeight
        )
        circleDrawable.draw(canvas)
        myIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     *   내 위치 마커로 계속 업데이트
     *   현재 위치(currentLocation)
     */
    private fun setMyIconToMap() {
        if (currentMarker != null) currentMarker!!.remove() // 이전 마커 지워주는 행동

        val markerOptions = MarkerOptions()
        markerOptions.position(currentLocation)
        markerOptions.title("Me")
        markerOptions.icon(myIcon)
        currentMarker = mMap!!.addMarker(markerOptions) // 이게 내 아이콘 찍네 => 지도에 현재 위치 찍는 듯?

        /**
         *   현재위치 따라서 카메라 이동
         */
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
//        mMap!!.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                currentLocation,
//                17F
//            )
//        )
    }


    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}