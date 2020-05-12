package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.customUI.WorkaroundMapFragment
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.gpx.WayPointType.FINISH_POINT
import com.umpa2020.tracer.gpx.WayPointType.START_POINT
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.main.start.racing.RacingSelectPeopleActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.BaseFB.Companion.MAKER_ID
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.util.Chart
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RankingMapDetailActivity : AppCompatActivity(), OnSingleClickListener, OnMapReadyCallback {
  lateinit var routeGPX: RouteGPX
  var dbMapId = ""
  lateinit var traceMap: TraceMap
  var mapId = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ranking_map_detail)

    val smf =
      supportFragmentManager.findFragmentById(R.id.rankingDetailMapViewer) as SupportMapFragment
    smf.getMapAsync(this)

    val intent = intent
    //전달 받은 값으로 Title 설정
    mapId = intent.extras?.getString("mapId").toString()
    val mapTitle = mapId.subSequence(0, mapId.length - TIMESTAMP_LENGTH)
    val time = mapId.subSequence(mapId.length - TIMESTAMP_LENGTH, mapId.length) as String
    intent.getStringExtra("asd")
    rankingDetailMapTitle.text = mapTitle

    rankingDetailDate.text = time.toLong().format("yyyy-MM-dd HH:mm:ss")
    (smf as WorkaroundMapFragment)
      .setListener(object : WorkaroundMapFragment.OnTouchListener {
        override fun onTouch() {
          rankingDetailScrollView.requestDisallowInterceptTouchEvent(true);
        }
      })

    // TODO : 윤권이랑 같이 빼자 이거

    val db = FirebaseFirestore.getInstance()

    db.collection("maps")
      .whereEqualTo("mapId", mapId)
      .get()
      .addOnSuccessListener {
          val infoData = it.documents.first().toObject(InfoData::class.java)
          MainScope().launch {
            FBProfileRepository().getProfileImage(infoData!!.makerId!!)?.let {
              rankingDetailProfileImage.image(it)
            }
          }
          val storage = FirebaseStorage.getInstance()
          val routeRef = storage.reference.child(infoData!!.routeGPXPath.toString())
          val localFile = File.createTempFile("routeGpx", "xml")
          routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
            routeGPX = localFile.path.gpxToClass()
            // 1차원 배열인 고도는 그대로 받아오면 되고
            val speedList = mutableListOf<Double>()
            val elevationList = mutableListOf<Double>()
            routeGPX.trkList.forEach { wpt ->
              wpt.speed?.let { speedList.add(it) }
              elevationList.add(wpt.alt)
            }

            // 실행 수 및 db에 있는 맵타이틀을 알기위해서 (구분 시간 값 포함)
            dbMapId = infoData!!.mapId!!

            handler.sendEmptyMessage(0)

            // ui 스레드 따로 빼주기
            runOnUiThread {
              MainScope().launch {
                withContext(Dispatchers.IO) {
                  FBProfileRepository().getUserNickname(infoData.makerId!!)
                }.let {
                  rankingDetailNickname.text = it
                }
              }
              rankingDetailMapDetail.text = infoData.mapExplanation
              rankingDetailDistance.text = String.format("%.2f", infoData.distance!! / 1000)
              rankingDetailTime.text = infoData.time!!.format(m_s)
              rankingDetailSpeed.text = String.format("%.2f", speedList.average())
              val chart = Chart(elevationList, speedList, rankingDetailChart)
              chart.setChart()
            }
          }
        //FBMapImage().getMapImage(rankingDetailGoogleMap, mapId)
        rankingDetailRaceButton.setOnClickListener(this)
      }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.rankingDetailRaceButton -> { //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        //showPopup()

        val nextIntent = Intent(this, RacingSelectPeopleActivity::class.java)
        nextIntent.putExtra("mapId", mapId)
        nextIntent.putExtra("RouteGPX", routeGPX)
        startActivity(nextIntent)
      }
    }
  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopup // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  private fun showPopup() {
    noticePopup = ChoicePopup(this, getString(R.string.select_type),
      getString(R.string.how_type),
      getString(R.string.recording), getString(R.string.public_),
      View.OnClickListener {
        //랭킹 기록용 버튼 눌렀을 때
        val intent = Intent(App.instance.context(), RacingActivity::class.java)
        intent.putExtra("RouteGPX", routeGPX)
        intent.putExtra("mapId", dbMapId)
        noticePopup.dismiss()
        startActivity(intent)
        finish()
      },
      View.OnClickListener {
        noticePopup.dismiss()
      })
    noticePopup.show()

  }

  override fun onMapReady(googleMap: GoogleMap) {
    traceMap = TraceMap(googleMap) //구글맵
    handler.sendEmptyMessage(0)
  }

  var bDraw = false
  val handler = object : Handler() {
    override fun handleMessage(msg: Message) {
      when (msg.what) {
        0 -> {
          if (bDraw) {
            //TODO : 이렇게 해야하나..?
            traceMap.drawRoute(routeGPX.trkList.toList(), routeGPX.wptList.filter { it.type == START_POINT || it.type == FINISH_POINT })
          } else bDraw = true
        }
      }
    }
  }
}
