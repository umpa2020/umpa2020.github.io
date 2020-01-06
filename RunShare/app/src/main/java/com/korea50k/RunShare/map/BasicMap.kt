package com.korea50k.RunShare.map

import android.content.Context
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.korea50k.RunShare.R
import android.graphics.Canvas
import com.korea50k.RunShare.dataClass.UserState


class BasicMap : OnMapReadyCallback {
    lateinit var mMap: GoogleMap    //racingMap 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0, 0.0)          //이전위치
    lateinit var cur_loc: LatLng            //현재위치
    lateinit var context: Context
    lateinit var userState: UserState
    lateinit var currentMarker: Marker
    lateinit var racerIcon: BitmapDescriptor

    //Running
    constructor(smf: SupportMapFragment, context: Context) {
        this.context = context
        smf.getMapAsync(this)
        initLocation()
        userState = UserState.NORMAL
        print_log("Set UserState NORMAL")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prev_loc, 17F))
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun startTracking() {

    }

    fun stopTracking() {

    }

    fun pauseTracking() {
        print_log("pause")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        userState = UserState.PAUSED
        print_log("Set UserState PAUSED")
    }

    fun restartTracking() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        userState=UserState.NORMAL
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
                }
            }
            .addOnFailureListener {
                //TODO:GPS 상태를 확인하세요!
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
                        print_log("Basic Map Log")
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
                    }
                }
            }
        }
    }
    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }
}