package com.umpa2020.tracer.main.start

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.main.ranking.RankingMapDetailActivity
import com.umpa2020.tracer.main.start.racing.RankingRecodeRacingActivity
import com.umpa2020.tracer.main.start.running.RunningActivity
import com.umpa2020.tracer.network.FBMap
import com.umpa2020.tracer.trace.TraceMap
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.PrettyDistance
import com.umpa2020.tracer.util.UserInfo
import com.umpa2020.tracer.util.gpx.GPXConverter
import kotlinx.android.synthetic.main.fragment_start.view.*
import java.io.File


class StartFragment : Fragment(), OnMapReadyCallback,View.OnClickListener {
  val TAG = "StartFragment"

  lateinit var traceMap: TraceMap

  lateinit var currentLocation: Location

  lateinit var locationBroadcastReceiver: BroadcastReceiver
  var routeMarkers = mutableListOf<Marker>()
  // 처음 화면 시작에서 주변 route 마커 찍어주기 위함
  val STRAT_FRAGMENT_NEARMAP = 30
  val NEARMAPFALSE = 41
  var nearMaps: ArrayList<NearMap> = arrayListOf()
  var moveCamera=true

  override fun onClick(v: View) {
    when (v.id) {

      R.id.mainStartRunning -> {
        val newIntent = Intent(activity, RunningActivity::class.java)
        startActivity(newIntent)
      }

      R.id.mainStartRacing -> {
        val bound = traceMap.mMap.projection.visibleRegion.latLngBounds

        val mHandler = object : Handler(Looper.getMainLooper()) {
          override fun handleMessage(msg: Message) {
            when (msg.what) {
              STRAT_FRAGMENT_NEARMAP -> {
                nearMaps = msg.obj as ArrayList<NearMap>
                Logg.d("ssmm11 nearMaps = $nearMaps")
                val icon =
                  BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)

                routeMarkers.forEach {
                  it.remove()
                }
                routeMarkers.clear()
                nearMaps.forEach {
                  val mapTitle = it.mapTitle.split("||")
                  //데이터 바인딩
                  routeMarkers.add(
                    traceMap.mMap.addMarker(
                      MarkerOptions()
                        .position(it.latLng)
                        .title(mapTitle[0])
                        .snippet(PrettyDistance().convertPretty(it.distance))
                        .icon(icon)
                    )
                  )
                  //TODO: 윈도우 커스터마이즈
                  routeMarkers.last().tag = it.mapTitle

                  traceMap.mMap.setOnInfoWindowClickListener { it2 ->
                    val intent = Intent(activity, RankingMapDetailActivity::class.java)
                    intent.putExtra("MapTitle", it2.tag.toString())
                    startActivity(intent)
                  }
                }
              }
              NEARMAPFALSE -> {
                // 빈 상태
              }
            }
          }
        }
        FBMap().getNearMap(bound.southwest, bound.northeast, mHandler)
        /*val newIntent = Intent(activity, NearRouteActivity::class.java)
        newIntent.putExtra("currentLocation", currentLocation.toLatLng()) //curLoc 정보 인텐트로 넘김
        startActivity(newIntent)*/
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    Logg.d("onCreateView()")
    val view = inflater.inflate(R.layout.fragment_start, container, false)
    view.test.setOnClickListener {
      Logg.d("test 실행")
      val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
      val routeRef = storage.reference.child("mapRoute").child("Short SanDiego route||1586002359186")
      val localFile = File.createTempFile("routeGpx", "xml")

      routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
        val routeGPX = GPXConverter().GpxToClass(localFile.path)
        val intent = Intent(context, RankingRecodeRacingActivity::class.java)
        intent.putExtra("RouteGPX", routeGPX)
        intent.putExtra("mapTitle", "Short SanDiego route||1586002359186")
        startActivity(intent)
      }
      routeRef.downloadUrl.addOnCompleteListener {
      }
    }

    val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
    smf.getMapAsync(this)
    locationBroadcastReceiver = object : BroadcastReceiver(){
      override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getParcelableExtra<Location>("message")
        currentLocation = message as Location
        if(moveCamera) traceMap.moveCamera(currentLocation.toLatLng())
      }
    }
    return view
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Logg.d("onViewCreated()")

    view.mainStartRunning.setOnClickListener(this)
    view.mainStartRacing.setOnClickListener(this)
  }

  override fun onResume() {
    super.onResume()
    // 브로드 캐스트 등록 - 전역 context로 수정해야함
    LocalBroadcastManager.getInstance(this.requireContext())
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))
  }

  override fun onPause() {
    super.onPause()
//    UserInfo.rankingLatLng = currentLocation.toLatLng()
    //        브로드 캐스트 해제 - 전역 context로 수정해야함
    LocalBroadcastManager.getInstance(this.requireContext()).unregisterReceiver(locationBroadcastReceiver)
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
  }
}