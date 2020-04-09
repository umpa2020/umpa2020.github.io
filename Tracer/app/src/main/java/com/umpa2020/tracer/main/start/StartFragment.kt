package com.umpa2020.tracer.main.start

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.main.ranking.RankingMapDetailActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.main.start.running.RunningActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.FBMap
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.PrettyDistance
import com.umpa2020.tracer.util.UserInfo
import com.umpa2020.tracer.util.gpx.GPXConverter
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import java.io.File


class StartFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {
  val TAG = "StartFragment"

  lateinit var traceMap: TraceMap
  var currentLocation: Location? = null

  lateinit var locationBroadcastReceiver: BroadcastReceiver
  var routeMarkers = mutableListOf<Marker>()

  // 처음 화면 시작에서 주변 route 마커 찍어주기 위함
  val STRAT_FRAGMENT_NEARMAP = 30
  val NEARMAPFALSE = 41
  var nearMaps: ArrayList<NearMap> = arrayListOf()
  var wedgedCamera = true
  val progressBar = MyProgressBar()


  override fun onClick(v: View) {
    when (v.id) {
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
      R.id.mainStartBackButton -> {
        mainStartLogoTextView.visibility = View.VISIBLE
        mainStartSearchLayout.visibility = View.GONE
        // 키보드 숨기기
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
          .hideSoftInputFromWindow(mainStartSearchTextView.windowToken, 0)
      }
    }
  }

  private fun searchThisArea() {
    val bound = traceMap.mMap.projection.visibleRegion.latLngBounds

    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          STRAT_FRAGMENT_NEARMAP -> {

            nearMaps = msg.obj as ArrayList<NearMap>
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
            mainStartSearchAreaButton.visibility = View.GONE
            progressBar.progressBarDismiss()

          }
          NEARMAPFALSE -> {
            progressBar.progressBarDismiss()
            // 빈 상태
          }
        }
      }
    }
    FBMap().getNearMap(bound.southwest, bound.northeast, mHandler)
  }

  private fun search() {
    val geocoder = Geocoder(context)
    if (mainStartSearchTextView.text.isEmpty()) {
      Toast.makeText(context, "Please enter some address", Toast.LENGTH_SHORT).show()
      return
    }
    val addressList =
      geocoder.getFromLocationName(mainStartSearchTextView.text.toString(), 10)
    // 최대 검색 결과 개수
    if (addressList.size == 0) {
      Toast.makeText(context, "Can't find location", Toast.LENGTH_SHORT).show()
    } else {
      mainStartSearchTextView.setText(addressList[0].getAddressLine(0))
      traceMap.moveCamera(LatLng(addressList[0].latitude, addressList[0].longitude))
      searchThisArea()
    }

    // 키보드 숨기기
    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
      .hideSoftInputFromWindow(mainStartSearchTextView.windowToken, 0)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    Logg.d("onCreateView()")
    val view = inflater.inflate(R.layout.fragment_start, container, false)
    view.test.setOnClickListener {
      Logg.d("test 실행")
      val storage = FirebaseStorage.getInstance()
      val routeRef = storage.reference.child("mapRoute").child("Short SanDiego route||1586002359186")
      val localFile = File.createTempFile("routeGpx", "xml")

      routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
        val routeGPX = GPXConverter().GpxToClass(localFile.path)
        val intent = Intent(context, RacingActivity::class.java)
        intent.putExtra("RouteGPX", routeGPX)
        intent.putExtra("mapTitle", "Short SanDiego route||1586002359186")
        startActivity(intent)
      }
      routeRef.downloadUrl.addOnCompleteListener {
      }
    }

    view.mainStartSearchTextView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        Logg.i("엔터키 클릭")
        search()
        return true
      }
    })

    val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
    smf.getMapAsync(this)
    locationBroadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getParcelableExtra<Location>("message")
        currentLocation = message as Location
        if (wedgedCamera) traceMap.moveCamera(currentLocation!!.toLatLng())
        if (progressBar.mprogressBar.isShowing) {
          searchThisArea()
          progressBar.progressBarDismiss()
        }
      }
    }
    return view
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.
    traceMap.mMap.setOnCameraMoveListener {
      wedgedCamera = false
      mainStartSearchAreaButton.visibility = View.VISIBLE
    }
    traceMap.mMap.setOnMyLocationButtonClickListener {
      wedgedCamera = true
      true
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Logg.d("onViewCreated()")

    view.mainStartRunning.setOnClickListener(this)
    view.mainStartSearchAreaButton.setOnClickListener(this)
    view.mainStartSearchButton.setOnClickListener(this)
    view.mainStartBackButton.setOnClickListener(this)
  }

  override fun onResume() {
    super.onResume()
    // 브로드 캐스트 등록 - 전역 context로 수정해야함
    LocalBroadcastManager.getInstance(this.requireContext())
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))

    progressBar.progressBarShow(2)
  }

  override fun onPause() {
    super.onPause()
    UserInfo.rankingLatLng = currentLocation?.toLatLng()
    //        브로드 캐스트 해제 - 전역 context로 수정해야함
    LocalBroadcastManager.getInstance(this.requireContext()).unregisterReceiver(locationBroadcastReceiver)
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
  }
}
