package com.umpa2020.tracer.main.start.racing

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.ARRIVE_BOUNDARY
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION_COUNT
import com.umpa2020.tracer.constant.Constants.Companion.RACE_RESULT
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RacerData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.main.start.racing.RacingSelectPeopleActivity.Companion.RACER_LIST
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.TTS
import kotlinx.android.synthetic.main.activity_running.*
import kotlinx.coroutines.*
import java.io.File

class RacingActivity : BaseRunningActivity() {
  companion object {
    const val ROUTE_GPX = "RouteGPX"
  }

  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapId: String
  var racingResult = true
  var deviationCount = 0

  lateinit var turningPointList: MutableList<Marker>
  lateinit var markerList: MutableList<Marker>
  var track: MutableList<LatLng> = mutableListOf()
  var nextWP: Int = 1
  var nextTP: Int = 0
  lateinit var racerList: Array<RacerData>
  var racerGPXList: List<RouteGPX>? = null
  lateinit var startPoint: WayPoint
  lateinit var mCustomMarkerView: View
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val routeGPXUri = intent.getStringExtra(ROUTE_GPX)
    runningAppBarTextView.text = getString(R.string.race)
    mapRouteGPX = Uri.parse(routeGPXUri).gpxToClass()
    mapId = intent.getStringExtra(MAP_ID)!!
    racerList = intent.getSerializableExtra(RACER_LIST) as Array<RacerData>

    Logg.d(racerList.joinToString())
    launch {
      racerGPXList = FBRacingRepository().listRacingGPX(mapId, racerList.map { it.racerId })
      loadRoute()
    }

    TTS.speech(getString(R.string.goToStartPoint))
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)
    val progressBar = MyProgressBar()
    progressBar.show()
    
    traceMap.drawRoute(mapRouteGPX.trkList, mapRouteGPX.wptList).run {
      markerList = this.first
      turningPointList = this.second
    }
    progressBar.dismiss()
  }

  override fun init() {
    super.init()
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)
    mCustomMarkerView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.profile_marker, null)
    runningStopButton.setOnLongClickListener {
      noticePopup = ChoicePopup(this, getString(R.string.please_select),
        getString(R.string.cannot_save),
        getString(R.string.yes), getString(R.string.no),
        View.OnClickListener {
          noticePopup.dismiss()
          finish()
        },
        View.OnClickListener {
          noticePopup.dismiss()
        })
      noticePopup.show()
      true
    }
  }

  fun loadRoute() {
    mapRouteGPX.trkList.forEach {
      track.add(it.toLatLng())
    }
    mapRouteGPX.wptList.let { startPoint = it.first() }
  }

  override fun onSingleClick(v: View?) {
    Logg.d("tlqkf!! before when")
    when (v!!.id) {
      R.id.runningStartButton -> {
        Logg.d("tlqkf!! tqtqtqt")
        when (userState) {
          UserState.NORMAL -> {
            Toast.makeText(
              this,
              getString(R.string.startpoint) + ARRIVE_BOUNDARY + getString(R.string.only_start),
              Toast.LENGTH_LONG
            ).show()
            Logg.d("NORMAL")
          }
          UserState.READYTORACING -> {
            Logg.d("READYTORACING")
            runningNotificationTextView.visibility = View.GONE
            start(getString(R.string.startRacing)) // tts String 전달
          }
          else -> {
          }
        }
      }
      R.id.runningStopButton -> {
        //"종료를 원하시면 길게 눌러주세요".show()
        Toast.makeText(this, getString(R.string.press_hold), Toast.LENGTH_LONG).show()

      }
      R.id.runningPauseButton -> {
        if (privacy == Privacy.RACING) {
          showPausePopup(
            getString(R.string.pause_notsave)
          )
        } else {
          if (userState == UserState.PAUSED)
            restart()
          else
            pause()
        }
      }
    }
  }


  override fun updateLocation(curLoc: Location) {
    super.updateLocation(curLoc)
    when (userState) {
      UserState.NORMAL -> {
        Logg.d("NORMAL")
        checkIsReady()
      }

      UserState.READYTORACING -> {
        Logg.d("READYTORACING")
        checkIsReadyToRacing()
      }
      UserState.RUNNING -> {
        Logg.d("RUNNING")
        checkMarker()
        checkTurningPoint()
        checkDeviation()
      }
      else -> {

      }
    }
  }


  override fun start(tts: String) {
    super.start(tts)
    FBMapRepository().incrementExecute(mapId)
    wpList.add(
      currentLocation!!.toWayPoint(START_POINT)
    )

    if (!racerGPXList.isNullOrEmpty()) {
      Logg.d("Start Virtual Racing")
      virtualRacing()
    }
  }

  /**
   * 가상레이싱 코루틴 함수
   * 다른 사람의 GPX를 읽어서 자동으로 각 체크포인트의 이동시간을 계산하여 마커를 이동시킨다
   */

  private fun virtualRacing() {
    val checkPoints = arrayOf(DISTANCE_POINT, START_POINT, FINISH_POINT)
    racerGPXList?.forEachIndexed { racerNo, racingGPX ->
      launch {
        val wpts = racingGPX.wptList.filter { checkPoints.contains(it.type) }
        var tempIndex = 1
        val wptIndices = mutableListOf<Int>()
        wptIndices.add(0)
        wptIndices.addAll(racingGPX.trkList.mapIndexed { i, trk ->
          if (trk.toLatLng() == wpts[tempIndex].toLatLng()) {
            tempIndex++
            i
          } else null
        }.filterNotNull())

        withContext(Dispatchers.Main) {
          traceMap.addRacer(wpts[0].toLatLng(), racerList[racerNo], mCustomMarkerView)
          run loop@{
            wpts.forEachIndexed { index, it ->
              if (index + 2 == wpts.size) return@loop
              val duration = (wpts[index + 1].time!! - wpts[index].time!!)
              val unitDuration = duration / (wptIndices[index + 1] - wptIndices[index])

              Logg.d("기간 $unitDuration  1 : ${wpts[index + 1].time}   2: ${wpts[index].time}")
              (wptIndices[index]..wptIndices[index + 1]).forEach {
                delay(unitDuration)
                traceMap.updateMarker(racerNo, racingGPX.trkList[it].toLatLng())
              }
            }
          }
          TTS.speech("${racerList[racerNo].racerName} is arrive")
          traceMap.removeRacer(racerNo)
        }
      }
    }
  }


  override fun stop(tts: String) {
    super.stop(tts)
    wpList.add(currentLocation!!.toWayPoint(FINISH_POINT))

    val infoData = MapInfo()
    infoData.time = SystemClock.elapsedRealtime() - runningTimerTextView.base
    infoData.mapId = mapId
    infoData.distance = distance
    infoData.challenge = false
    val routeGPX = RouteGPX(infoData.time, "", wpList, trkList)

    val saveFolder = File(App.instance.filesDir, "RouteGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    routeGPX.addDirectionSign()
    val routeGpxUri = routeGPX.classToGpx(saveFolder.path).toString()

    val newIntent = Intent(this, RacingFinishActivity::class.java)
    newIntent.putExtra(RACE_RESULT, racingResult)
    newIntent.putExtra("InfoData", infoData)
    newIntent.putExtra(ROUTE_GPX, routeGpxUri)
    startActivity(newIntent)
    finish()
  }


  private fun checkTurningPoint() {
    if (nextTP >= turningPointList.size) return

    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        turningPointList[nextTP].position
      ) < ARRIVE_BOUNDARY
    ) {
      TTS.speech(turningPointList[nextTP].title)
      nextTP++
    }
  }

  private fun checkMarker() {
    if (nextWP == markerList.size) return

    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        markerList[nextWP].position
      ) < ARRIVE_BOUNDARY
    ) {
      traceMap.changeMarkerIcon(nextWP)
      nextWP++
      wpList.add(currentLocation.toWayPoint(DISTANCE_POINT))
      if (nextWP == markerList.size) {
        stop(getString(R.string.finishRacing))
      }
    }
  }

  private fun checkDeviation() {
    Logg.d(
      PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      ).toString()
    )
    //경로이탈검사
    if (PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      )
    ) {
      deviationCount = 0
      runningNotificationTextView.visibility = View.GONE
    } else {
      if (userState != UserState.RUNNING) return
      if (deviationCount == DEVIATION_COUNT) {
        racingResult = false
        pause()
        notice(getString(R.string.notice_msg_pause_deviation))
        return
      }
      deviationCount++
      notice(
        getString(R.string.out_of_route) + deviationCount.toString() + getString(R.string.route_deviates) + DEVIATION_COUNT + getString(
          R.string.resgisration
        )
      )

    }
  }


  private fun checkIsReadyToRacing() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) > ARRIVE_BOUNDARY
    ) {
      userState = UserState.NORMAL
      notice(
        getString(R.string.move_startpoint)
          + (SphericalUtil.computeDistanceBetween(
          currentLatLng,
          markerList[0].position
        )).prettyDistance
      )
    }
  }

  //시작포인트에 10m이내로 들어오면 준비상태로 변경
  private fun checkIsReady() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) <= ARRIVE_BOUNDARY
    ) {
      notice(getString(R.string.want_start))
      //traceMap.changeMarkerColor(0, BitmapDescriptorFactory.HUE_BLUE)
      userState = UserState.READYTORACING
    } else {
      notice(
        getString(R.string.move_startpoint)
          + (SphericalUtil.computeDistanceBetween(
          currentLatLng,
          mapRouteGPX.wptList[0].toLatLng()
        )).prettyDistance
      )
    }
  }


}
