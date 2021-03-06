package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.customUI.WorkaroundMapFragment
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPointType.FINISH_POINT
import com.umpa2020.tracer.gpx.WayPointType.START_POINT
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity.Companion.ROUTE_GPX
import com.umpa2020.tracer.main.start.racing.RacingSelectPeopleActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBStorageRepository
import com.umpa2020.tracer.util.Chart
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.coroutines.launch
import java.io.File

class RankingMapDetailActivity : BaseActivity(), OnSingleClickListener, OnMapReadyCallback {
  lateinit var routeGPX: RouteGPX
  lateinit var traceMap: TraceMap
  var mapId = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ranking_map_detail)
    val progressBar = MyProgressBar()
    progressBar.show()
    //전달 받은 값으로 Title 설정
    mapId = intent.extras?.getString(MAP_ID).toString()

    val smf =
      supportFragmentManager.findFragmentById(R.id.rankingDetailMapViewer) as SupportMapFragment
    smf.getMapAsync(this)

    (smf as WorkaroundMapFragment)
      .setListener(object : WorkaroundMapFragment.OnTouchListener {
        override fun onTouch() {
          rankingDetailScrollView.requestDisallowInterceptTouchEvent(true);
        }
      })
    rankingDetailRaceButton.setOnClickListener(this)
    launch {
      FBMapRepository().getMapInfo(mapId)?.let {
        rankingDetailMapTitle.text = it.mapTitle
        rankingDetailDate.text = it.createTime.format(Y_M_D)
        rankingDetailMapDetail.text = it.mapExplanation
        rankingDetailDistance.text = String.format("%.2f", it.distance / 1000)
        rankingDetailTime.text = it.time.format(m_s)
        rankingDetailSpeed.text = it.averageSpeed.prettyDistance()
        FBStorageRepository().getFile(it.routeGPXPath).gpxToClass().let {
          routeGPX = it
          val speedList = mutableListOf<Double>()
          val elevationList = mutableListOf<Double>()
          it.trkList.forEach { wpt ->
            wpt.speed?.let { speedList.add(it) }
            elevationList.add(wpt.alt)
          }
          val chart = Chart(elevationList, speedList, rankingDetailChart)
          chart.setChart()
          traceMap.drawRoute(it.trkList.toList(), it.wptList.filter { it.type == START_POINT || it.type == FINISH_POINT })
        }
        FBProfileRepository().getProfileImage(it.makerId)?.let {
          rankingDetailProfileImage.image(it)
        }
        FBProfileRepository().getUserNickname(it.makerId).let {
          rankingDetailNickname.text = it
          progressBar.dismiss()
        }
      }
    }
  }


  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.rankingDetailRaceButton -> { //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        val nextIntent = Intent(this, RacingSelectPeopleActivity::class.java)
        val saveFolder = File(App.instance.filesDir, "RouteGPX") // 저장 경로
        if (!saveFolder.exists()) {       //폴더 없으면 생성
          saveFolder.mkdir()
        }
        val routeGpxUri = routeGPX.classToGpx(saveFolder.path).toString()

        nextIntent.putExtra(MAP_ID, mapId)
        nextIntent.putExtra(ROUTE_GPX, routeGpxUri)
        startActivity(nextIntent)
      }
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    traceMap = TraceMap(googleMap) //구글맵
  }
}

