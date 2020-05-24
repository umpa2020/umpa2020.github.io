package com.umpa2020.tracer.main.start

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPointType
import com.umpa2020.tracer.main.challenge.ChallengeDataSettingActivity
import com.umpa2020.tracer.main.ranking.RankingMapDetailActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.main.start.running.RunningActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBStorageRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import kotlinx.coroutines.*
import java.io.File


class StartFragment : Fragment(), OnMapReadyCallback, OnSingleClickListener,CoroutineScope by MainScope() {
  var traceMap: TraceMap? = null
  var currentLocation: Location? = null
  var routeMarkers = mutableListOf<Marker>()

  // 처음 화면 시작에서 주변 route 마커 찍어주기 위함
  val STRAT_FRAGMENT_NEARMAP = 30
  val NEARMAPFALSE = 41
  var nearMaps: ArrayList<MapInfo> = arrayListOf()
  var wedgedCamera = true // 카메로 고정 flag
  val progressBar = MyProgressBar()
  var firstFlag = true
  lateinit var mCustomMarkerView: View
  var zoomLevel: Float? = 16f // 줌 레벨 할당

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.mainStartRunning -> {
        val newIntent = Intent(activity, RunningActivity::class.java)
        startActivity(newIntent)
      }
      R.id.mainStartSearchAreaButton -> {
        searchThisArea()
      }
      R.id.mainStartSearchButton -> {
        if (mainStartLogoTextView.visibility == View.VISIBLE) {
          mainStartLogoTextView.visibility = View.GONE
          mainStartSearchLayout.visibility = View.VISIBLE
        } else {
          search()
          wedgedCamera = false
        }
      }
      R.id.mainStartBackButton -> { // 검색창에서 뒤로가기 버튼
        mainStartLogoTextView.visibility = View.VISIBLE
        mainStartSearchLayout.visibility = View.GONE
        // 키보드 숨기기
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
          .hideSoftInputFromWindow(mainStartSearchTextView.windowToken, 0)

        // EditText 초기화
        mainStartSearchTextView.setText("")
      }
      R.id.upload -> {
        val intent = Intent(context, ChallengeDataSettingActivity::class.java)
        startActivity(intent)
      }
      R.id.gpxTest -> {
        val a = Uri.parse("/data/data/com.umpa2020.tracer/files/routeGPX/2020korea50k_50 final.txt")
        val gpx = a.gpxToClass()
        gpx.addCheckPoint()
        gpx.addDirectionSign()
        gpx.wptList.forEachIndexed { i, it ->

        }

        val saveFolder = File(requireContext().filesDir, "routeGPX") // 저장 경로
        if (!saveFolder.exists()) {       //폴더 없으면 생성
          saveFolder.mkdir()
        }
        val routeGpxUri = gpx.classToGpx(saveFolder.path).toString()
        Logg.d("$routeGpxUri")
      }
    }
  }
  val jobList= mutableListOf<Job>()
  /**
   *  현재 맵 보이는 범위로 루트 검색
   */
  private fun searchThisArea() {
    Logg.d("1번선수")
    progressBar.show()
    val bound = traceMap!!.mMap.projection.visibleRegion.latLngBounds
    launch {
      FBMapRepository().listNearMap(bound.southwest, bound.northeast).let { nearMapList ->
        if (nearMapList.isEmpty()) {
          getString(R.string.not_search).show()
          progressBar.dismiss()
        } else {
          mainStartSearchAreaButton?.visibility = View.GONE
          routeMarkers.forEach {
            it.remove()
          }
          routeMarkers.clear()
          nearMapList.forEach { nearMap ->
            //데이터 바인딩
            Logg.d("asd : ${FBProfileRepository().getProfile(nearMap.makerId).imgPath}")
            routeMarkers.add(
              traceMap!!.mMap.addMarker(
                MarkerOptions()
                  .position(LatLng(nearMap.startLatitude, nearMap.startLongitude))
                  .title(nearMap.mapTitle)
                  .snippet(nearMap.distance.prettyDistance)
                  .icon(traceMap?.makeProfileIcon(mCustomMarkerView, nearMap.makerId))

              ).apply {
                Logg.d("nani0-2")
                tag = nearMap.mapId
              }
            )
          }
          progressBar.dismiss()
        }
      }
    }
  }

  /**
   *  검색 창에 입력한 장소 주소로 반환 및 검색창에 설정.
   */
  private fun search() {
    val geocoder = Geocoder(context)
    if (mainStartSearchTextView.text.isEmpty()) {
      getString(R.string.enter_address).show()
      return
    }

    val addressList =
      geocoder.getFromLocationName(mainStartSearchTextView.text.toString(), 10)

    Logg.i(addressList.size.toString())
    for (i in 0 until addressList.size)
      Logg.i(addressList[i].toString())

    // 최대 검색 결과 개수
    if (addressList.size == 0) {
      getString(R.string.cannot_find).show()
    } else {
      // mainStartSearchTextView.setText(addressList[0].getAddressLine(0))
      traceMap!!.moveCamera(LatLng(addressList[0].latitude, addressList[0].longitude), zoomLevel!!)
      searchThisArea()
    }

    // 키보드 숨기기
    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
      .hideSoftInputFromWindow(mainStartSearchTextView.windowToken, 0)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {

    Logg.d("onCreateView()")
    val view = inflater.inflate(R.layout.fragment_start, container, false)

    view.test.setOnClickListener {
      val storage = FirebaseStorage.getInstance()
      val routeRef = storage.reference.child("mapRoute").child("asdasdqwe1587633430060")
      val localFile = File.createTempFile("routeGpx", "xml")
      routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
        val routeGPX = Uri.fromFile(localFile).gpxToClass()
        val intent = Intent(App.instance.context(), RacingActivity::class.java)
        intent.putExtra("RouteGPX", routeGPX)
        val racingGPXs = ArrayList<RouteGPX>()
        racingGPXs.add(routeGPX)
        racingGPXs.add(routeGPX)
        racingGPXs.add(routeGPX)

        intent.putExtra("RacingGPXs", racingGPXs)
        intent.putExtra("mapTitle", "asdasdqwe1587633430060")
        startActivity(intent)
      }
    }

    // 검색 창 키보드에서 엔터키 리스너
    view.mainStartSearchTextView.setOnEditorActionListener { p0, p1, p2 ->
      Logg.i("엔터키 클릭")
      search()
      true
    }

    val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
    smf.getMapAsync(this)
    mCustomMarkerView = (activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.profile_marker, null);
    return view
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")

    traceMap = TraceMap(googleMap) //구글맵
    traceMap!!.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    traceMap!!.mMap.setMaxZoomPreference(18.0f) // 최대 줌 설정


    // Shared의 값을 받아와서 초기 카메라 위치 설정.
    Logg.d("${UserInfo.lat} , ${UserInfo.lng}")

    val latlng = LatLng(UserInfo.lat.toDouble(), UserInfo.lng.toDouble())
    traceMap!!.initCamera(latlng)

    // 카메라 이동 중일 때
    traceMap!!.mMap.setOnCameraMoveListener {
      wedgedCamera = false
      Logg.d("카메라 이동 중 $wedgedCamera")
      mainStartSearchAreaButton.visibility = View.GONE
    }

    // 카메라가 멈췄을 때
    traceMap!!.mMap.setOnCameraIdleListener {

      // 여기서 검색 버튼 보여주기
      zoomLevel = traceMap!!.mMap.cameraPosition.zoom
      Logg.d("카메라 멈춤 $wedgedCamera, 줌 레벨 : $zoomLevel")
      mainStartSearchAreaButton.visibility = View.VISIBLE
    }

    // 내 위치 버튼 클릭 리스너
    traceMap!!.mMap.setOnMyLocationButtonClickListener {
      traceMap!!.mMap
      wedgedCamera = true
      traceMap!!.moveCamera(currentLocation!!.toLatLng(), zoomLevel!!)
      Logg.d("내 위치로 카메라 이동 $wedgedCamera")
      true
    }

    // 맵 마커를 한 번 클릭했을 때, 해당 맵 자세히 보기 페이지로 넘어감
    traceMap!!.mMap.setOnInfoWindowClickListener { marker ->
      val intent = Intent(activity, RankingMapDetailActivity::class.java)
      intent.putExtra(MAP_ID, marker.tag.toString())
      startActivity(intent)
    }

    routeInit()

    traceMap!!.mMap.setOnMarkerClickListener {
      it.showInfoWindow()
     launch {
        if (it.tag != null) {
          FBMapRepository().getMapInfo(it.tag as String)?.let {
            FBStorageRepository().getFile(it.routeGPXPath).gpxToClass().let {
              drawMarkerRoute(it)
            }
          }
        }

      }
      true
    }
    traceMap!!.mMap.uiSettings.isCompassEnabled = true
    traceMap!!.mMap.uiSettings.isZoomControlsEnabled = true
  }

  override fun onResume() {
    super.onResume()
    // 브로드 캐스트 등록 - 전역 context로 수정해야함
    LocalBroadcastManager.getInstance(this.requireContext())
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))

    // Shared의 마지막 위치였던거 확인.
    Logg.d("${UserInfo.lat} , ${UserInfo.lng}")
  }

  fun getVersionInfo() {
    val info: PackageInfo =
      requireContext().packageManager.getPackageInfo(App.instance.packageName, 0)
    val version = info.versionName
    Logg.d(version.toString())
  }


  lateinit var loadTrack: Polyline

  /**
   * 마커 루트를 그리기 전에 초기화 작업
   */
  private fun routeInit() {
    val firstLatLng = mutableListOf<LatLng>()

    loadTrack =
      traceMap!!.mMap.addPolyline(
        PolylineOptions()
          .addAll(firstLatLng)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )
    loadTrack.tag = "init"
  }


  /**
   * 맵 마커를 누르면 끝나는 지점을 출력하고
   * 그 맵의 경로를 현재 맵에 보이게 표현
   */
  private fun drawMarkerRoute(gpx: RouteGPX) {
    val track = gpx.trkList.map { it.toLatLng() }

    // 폴리 라인만 그리는
    if (loadTrack.tag != "init") {
      loadTrack.remove()
      traceMap!!.markerList[0].remove()
      traceMap!!.markerList.removeAt(0)
    }

    loadTrack =
      traceMap!!.mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.BLACK)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )
    loadTrack.tag = "not init"
    gpx.wptList.forEachIndexed { i, it ->
      when (it.type) {
        WayPointType.FINISH_POINT -> {
          traceMap!!.markerList.add(
            traceMap!!.mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
//                .title(it.name)
                .icon(R.drawable.ic_finish_point.makingIcon())
            )
          )
        }
        else -> {
        }
      }
    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Logg.d("onViewCreated()")

    view.mainStartRunning.setOnClickListener(this)
    view.mainStartSearchAreaButton.setOnClickListener(this)
    view.mainStartSearchButton.setOnClickListener(this)
    view.mainStartBackButton.setOnClickListener(this)

    view.gpxTest.setOnClickListener(this)
    view.upload.setOnClickListener(this)
  }

  override fun onPause() {
    super.onPause()
    // 갑자기 뒤로가면 코루틴 취소
    MainScope().cancel()
    // 브로드 캐스트 해제
    LocalBroadcastManager.getInstance(this.requireContext())
      .unregisterReceiver(locationBroadcastReceiver)
    jobList.forEach {it.cancel() }
    cancel()
    //Shared로 마지막 위치 업데이트
    if (currentLocation != null) {
      Logg.d("마지막 위치 업데이트")
      UserInfo.lat = currentLocation!!.latitude.toFloat()
      UserInfo.lng = currentLocation!!.longitude.toFloat()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    zoomLevel = 16f
    Logg.d("onDestroy()")
  }

  // 초기에 위치 받아오면 카메라 설정.
  var locationBroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      val message = intent?.getParcelableExtra<Location>("message")
      currentLocation = message as Location
      if (wedgedCamera) traceMap!!.moveCamera(currentLocation!!.toLatLng(), zoomLevel!!)
      traceMap?.let {

        if (firstFlag) {
          searchThisArea()
          firstFlag = false
//          it.initCamera(currentLocation!!.toLatLng())
        }
//        Logg.d("${currentLocation}")
      }
    }
  }
}
