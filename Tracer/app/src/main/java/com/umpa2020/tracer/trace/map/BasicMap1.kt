package com.umpa2020.tracer.trace.map

/*
class BasicMap : OnMapReadyCallback{
    var mMap: GoogleMap? = null    //racingMap 인스턴스

    var TAG = "BasicMap"       //로그용 태그
    var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
    var currentLocation: LatLng = LatLng(0.0, 0.0)              //현재위치
    var context: Context
    var userState: UserState       //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
    var cameraFlag = false

    //Running
    constructor(smf: SupportMapFragment, context: Context) {    //객체 생성자
        this.context = context
        userState = UserState.NORMAL
        smf.getMapAsync(this)                                   //맵프레그먼트와 연결

    }

    var lastLocat : Location? = null
    override fun onMapReady(googleMap: GoogleMap) { //after the map is loaded
        Log.d("ssmm11", "onMapReady")
        mMap = googleMap //구글맵
        mMap!!.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    }

    fun setLocation(location: Location) {
        var lat = location!!.latitude
        var lng = location!!.longitude
        currentLocation = LatLng(lat, lng)

        if(!cameraFlag) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17F))   //화면이동
            cameraFlag = true
        }
        previousLocation = currentLocation                              //현재위치를 이전위치로 변경
    }
}*/