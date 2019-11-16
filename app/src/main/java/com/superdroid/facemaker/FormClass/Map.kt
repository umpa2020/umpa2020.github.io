package com.superdroid.facemaker.FormClass

import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.SphericalUtil
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Map:OnMapReadyCallback{
    lateinit var mMap: GoogleMap    //map 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback: LocationCallback
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    var prev_loc: LatLng = LatLng(0.0,0.0)          //이전위치
    lateinit var cur_loc: LatLng            //현재위치
    var latlngs: Vector<LatLng> = Vector<LatLng>()   //움직인 점들의 집합 나중에 저장될 점들 집합
    var route=ArrayList<LatLng>()     //로드할 점들의 집합
    lateinit var context: Context
    constructor(smf: SupportMapFragment,context: Context){
        this.context=context
        val mapFragment = smf
        mapFragment.getMapAsync(this)
        initLocation()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_loc,16F))
    }
    fun startTracking(){
        var locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for((i, location) in it.locations.withIndex()) {
                        var lat =location.latitude
                        var lng = location.longitude
                        cur_loc = LatLng(lat, lng)
                        /*if(prev_loc.latitude==cur_loc.latitude&&prev_loc.longitude==cur_loc.longitude) {
                            return  //움직임이 없다면 추가안함
                        }else if(false){ //비정상적인 움직임일 경우
                        }else {
                            latlngs.add(cur_loc)    //위 조건들을 통과하면 점 추가
                            mMap.addPolyline(
                                PolylineOptions().add(
                                    prev_loc,
                                    cur_loc
                                )
                            )   //맵에 폴리라인 추가
                            }
                           */
                            prev_loc = cur_loc                              //현재위치를 이전위치로 변경
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    cur_loc,
                                    16F
                                )
                            )        //현재위치 따라서 카메라 이동
                            print_log("위도 : "+lat.toString() + "경도 : " + lng.toString())

                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())        //위에서 설정한 Request와 Callback을 Looper단위로 실행

    }
    fun stop_Tracking():String{
        if(!fusedLocationClient.removeLocationUpdates(locationCallback).isSuccessful) {  //location 요청 종료
            var S: String = ""
            for (i in latlngs.indices) {             //위도 , 경도 \n 문자열 저장
                S += latlngs[i].latitude.toString() + "," + latlngs[i].longitude.toString() + "\n"

            }
            return S
        }
        else{
            print_log("Fail to stop")
            return "Fail"
        }
    }
    fun drawRoute() {                                                              //로드 된 경로 그리기
        var polyline = mMap.addPolyline(PolylineOptions().addAll(route))        //경로를 그릴 폴리라인 집합
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route[0],16F))                   //맵 줌
        polyline.setTag("A")
    }
    fun initLocation() {            //첫 위치 설정하고, prev_loc 설정
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if(location == null) {
                    print_log("Location is null")
                } else {
                    print_log("Success to get Init Location : "+location.toString())
                    prev_loc=LatLng(location.latitude,location.longitude)
                }
            }
            .addOnFailureListener {
                print_log("Error is "+it.message.toString())
                it.printStackTrace()
            }
    }
    fun getDistance(locations:ArrayList<LatLng>): Double {  //점들의 집합에서 거리구하기
        var distance=0.0
        var i=0;
        while(i<locations.size-1){
            distance+= SphericalUtil.computeDistanceBetween(locations[i], locations[i + 1])
            i++
        }
        return distance
    }
    fun print_log(text:String){
        Log.d(TAG,text.toString())
    }

}