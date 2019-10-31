package com.superdroid.facemaker

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import kotlinx.android.synthetic.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.jar.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.model.*
import org.w3c.dom.Text
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    var TAG = "Location Test"       //로그용 태그
    private lateinit var mMap: GoogleMap    //map 인스턴스
    lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스
    lateinit var locationCallback:LocationCallback

    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION)

    var prev_loc:LatLng = LatLng(0.0,0.0)          //이전위치
    lateinit var cur_loc:LatLng            //현재위치
    var latlngs:Vector<LatLng> =Vector<LatLng>()   //움직인 점들의 집합
    var route=ArrayList<LatLng>()     //로드할 점들의 집합

    lateinit var LoadFileName:String
    lateinit var saveFolder:File
    lateinit var tv_log:TextView
    //권한 체크
    private fun checkPermissions() {
        var rejectedPermissionList = ArrayList<String>()
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveFolder= File(filesDir,"mapdata"); // 저장 경로
                           //로드 할 점의 집합
        tv_log= findViewById<TextView>(R.id.log_save) //로그 용 텍스트뷰
        tv_log.movementMethod=ScrollingMovementMethod()

        val mapFragment = supportFragmentManager    //mapFragment 설정
            .findFragmentById(R.id.map_viewer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initLocation()              //
        checkPermissions()          //모든 권한 확인

        if(intent.hasExtra("FileName")){                    //로드가 된 경우
            LoadFileName=intent.getStringExtra("FileName")      //intent에서 불러올 파일이름 가져오기
            tv_log.text=LoadFileName
            readFile()      //불러올 파일 읽기
        }
    }

    private fun readFile() {                //불러온 파일 읽기
        try{
            var myfile=File(saveFolder,LoadFileName)            //saveFolder에 LoadFileName 파일
            var buf=BufferedReader(FileReader(myfile))             //버퍼리더
            var iterator=buf.lineSequence().iterator()      //버퍼리더의 이터레이터
            while(iterator.hasNext()){              //한줄 단위로 읽고, 줄이 끝나면 종료
                var s_line=iterator.next()
                route.add(LatLng(s_line.split(",")[0].toDouble(),s_line.split(",")[1].toDouble()))      //lat,lng 로 저장되어 있기 때문에 , 기준으로 스플릿해서 추가
            }
            buf.close()             //버퍼는 언제나 닫아야함 ㅇㅈ?
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
        tv_log.text=route.toString()
    }

    private fun initLocation() {            //첫 위치 설정하고, prev_loc 설정
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if(location == null) {
                    Log.e(TAG, "location get fail")
                } else {
                    Log.d(TAG, "${location.latitude} , ${location.longitude}")
                    prev_loc=LatLng(location.latitude,location.longitude)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "location error is ${it.message}")
                it.printStackTrace()
            }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_loc,16F))
        if(!route.isEmpty()) {      //불러온 루트가 있다면 그려랏!
            drawRoute()
        }
    }
    private fun drawRoute() {                                                              //로드 된 경로 그리기
        /*var route_t=ArrayList<LatLng>()
        route_t.add(LatLng(37.6196038,127.0586487))
        route_t.add(LatLng(37.6196161,127.0586498))
        route_t.add(LatLng(37.6196189,127.0586509))

        route_t.add( LatLng(37.6196208,127.0586544))
        route_t.add( LatLng(37.6196211,127.0586565))
        route_t.add( LatLng(37.6196191,127.0586524))
        route_t.add( LatLng(37.6195855,127.0586574))
        route_t.add( LatLng(37.6195646,127.0586523))
        route_t.add( LatLng(37.6195471,127.0586489))
        route_t.add( LatLng(37.6195327,127.0586457))
        route_t.add( LatLng(37.6195217,127.0586441))
        route_t.add( LatLng(37.6195078,127.0586448))
*/
        var polyline1 = mMap.addPolyline(PolylineOptions().addAll(route))        //경로를 그릴 폴리라인 집합
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route[0],16F))                   //맵 줌
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A")                                                               //폴리라인에 태그 설정 -> 나중에 스타일 할 때 필요
    }

    fun onClick_map(view: View) {
        when{
            view.id==R.id.btn_start -> start()
            view.id==R.id.btn_stop -> stop()
            view.id==R.id.btn_load -> load()
        }
    }

    fun start(){
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
                       /* if(prev_loc.latitude==cur_loc.latitude&&prev_loc.longitude==cur_loc.longitude){
                            return  //움직임이 없다면 추가안함
                        }else if(){ //비정상적인 움직임일 경우
                        }else { } */
                        latlngs.add(cur_loc)    //위 조건들을 통과하면 점 추가
                        mMap.addPolyline(PolylineOptions().add(prev_loc,cur_loc))   //맵에 폴리라인 추가
                        prev_loc=cur_loc                              //현재위치를 이전위치로 변경
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_loc, 16F))        //현재위치 따라서 카메라 이동
                        tv_log.text = tv_log.text.toString()+"위도 : "+lat.toString() +
                                "경도 : " + lng.toString() + "\n"
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())        //위에서 설정한 Request와 Callback을 Looper단위로 실행
    }

    fun stop(){
        if(!fusedLocationClient.removeLocationUpdates(locationCallback).isSuccessful){  //location 요청 종료
            var S:String=""
            for(i in latlngs.indices) {             //위도 , 경도 \n 문자열 저장
                S += latlngs[i].latitude.toString() + ","+ latlngs[i].longitude.toString()+"\n"
            }
            tv_log.text=saveFolder.path + "\n" + saveFolder.absolutePath + "\n"
            if(!saveFolder.exists()){       //폴더 없으면 생성
                saveFolder.mkdir()
            }
            try{
                val path="map"+saveFolder.list().size+".txt"        //폴더내 맵 개수 + 1 을 새로운 번호로 map생성
                var myfile=File(saveFolder,path)
                var buf = BufferedWriter( FileWriter( myfile, true))
                buf.append(S);
                buf.close();
                Toast.makeText(this,"succes" + myfile.path.toString(),Toast.LENGTH_SHORT).show()
            }catch(e:FileNotFoundException){
                e.printStackTrace()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }else{
            Toast.makeText(this,"Fail",Toast.LENGTH_SHORT).show()
        }

    }

    fun load(){             //load 액티비티 실행
        val nextIntent = Intent(this, LoadActivity::class.java)
        startActivity(nextIntent)
    }


}
//이 부분은 몰라도 돼용 메모장임
/* var ts:String=""
           for(i in saveFolder.listFiles()){
               ts+= i.name
           }
           tv.text=ts*/
/* fun zoomRoute(lstLatLngRoute:List<LatLng> ) { // 데이터 받고
        /*if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) {
          return;
        }*/
        var boundsBuilder = LatLngBounds.Builder();
        for ( latLngPoint:LatLng in lstLatLngRoute) {
          boundsBuilder.include(latLngPoint);
        }
        var latLngBounds = boundsBuilder.build();

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100), 200,
            object : CancelableCallback {
                override fun onFinish() {
                    drawRoutes()
                }
                override fun onCancel() {
                }
            })
    }
    fun drawRoutes() {/*
        var normalRoute = Route.Builder(mRouteOverlayView)
            .setRouteType(RouteType.PATH)
            .setCameraPosition(mMap.getCameraPosition())
            .setProjection(mMap.getProjection())
            .setLatLngs(mRoute)
            .setBottomLayerColor(Color.YELLOW)
            .setTopLayerColor(Color.RED)
            .create();

        Route dashRoute = new Route.Builder(mRouteOverlayView)
            .setRouteType(RouteType.DASH)
            .setCameraPosition(mMap.getCameraPosition())
            .setProjection(mMap.getProjection())
            .setLatLngs(LatlngData.getRouteB())
            .setDashColor(Color.BLACK)
            .create();

        Route arcRoute = new Route.Builder(mRouteOverlayView)
            .setRouteType(RouteType.ARC)
            .setCameraPosition(mMap.getCameraPosition())
            .setProjection(mMap.getProjection())
            .setLatLngs(LatlngData.getRouteB())
            .setBottomLayerColor(Color.GRAY)
            .setTopLayerColor(Color.BLACK)
            .setRouteShadowColor(Color.GRAY)
            .create();*/
    }
*/