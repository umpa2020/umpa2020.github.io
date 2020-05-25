package com.umpa2020.tracer.main.start.challengeracing

import android.content.Context
import android.content.Intent
import android.location.Location
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
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.ARRIVE_BOUNDARY
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION_COUNT
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBStorageRepository
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.TTS
import kotlinx.android.synthetic.main.activity_running.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

class ChallengeRacingActivity : BaseRunningActivity() {
  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapId: String
  var racingResult = true
  var deviationCount = 0
  lateinit var turningPointList: MutableList<Marker>
  lateinit var markerList: MutableList<Marker>
  var track: MutableList<LatLng> = mutableListOf()
  var nextWP: Int = 1
  var nextTP: Int = 0
  lateinit var startPoint: WayPoint
  lateinit var mCustomMarkerView: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    runningAppBarTextView.text = getString(R.string.challenge)
    mapId = intent.getStringExtra(MAP_ID)!!
    launch {
      mapRouteGPX = FBStorageRepository().getFile(FBMapRepository().getMapInfo(mapId)?.routeGPXPath!!).gpxToClass()
      loadRoute()
    }
    TTS.speech(getString(R.string.goToStartPoint))
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)

    val progressBar = MyProgressBar()
    progressBar.show()

    launch {
      while (!::mapRouteGPX.isInitialized) {
        delay(100)
      }
      traceMap.drawRoute(mapRouteGPX.trkList, mapRouteGPX.wptList).run {
        markerList = this.first
        turningPointList = this.second
      }
      progressBar.dismiss()
    }
  }

  override fun init() {
    super.init()
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)
    mCustomMarkerView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.profile_marker, null);
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
    when (v!!.id) {
      R.id.runningStartButton -> {
        when (userState) {
          UserState.NORMAL -> {
            Toast.makeText(
              this,
              getString(R.string.startpoint) + ARRIVE_BOUNDARY + getString(R.string.only_start),
              Toast.LENGTH_LONG
            ).show()

          }
          UserState.READYTORACING -> {

            runningNotificationTextView.visibility = View.GONE
            start(getString(R.string.startRacing))
          }
          else -> {
          }
        }
      }
      R.id.runningStopButton -> {
        this.toast(getString(R.string.press_hold))
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

        if (!::mapRouteGPX.isInitialized) return
        checkIsReady()
      }

      UserState.READYTORACING -> {

        checkIsReadyToRacing()
      }
      UserState.RUNNING -> {

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
      currentLocation.toWayPoint(START_POINT)
    )
  }

  override fun stop(tts: String) {
    super.stop(tts)
    wpList.add(currentLocation.toWayPoint(FINISH_POINT))

    val infoData = MapInfo()
    infoData.time = SystemClock.elapsedRealtime() - runningTimerTextView.base
    infoData.mapId = mapId
    infoData.distance = distance
    val routeGPX = RouteGPX(infoData.time, "", wpList, trkList)

    val newIntent = Intent(this, ChallengeRacingFinishActivity::class.java)
    newIntent.putExtra("Result", racingResult)
    newIntent.putExtra(Constants.CHALLENGE_ID, mapId)
    newIntent.putExtra(Constants.RACING_DISTANCE, distance)
    newIntent.putExtra("RecordList", recordList.toTypedArray().toLongArray())
    newIntent.putExtra("BestList", bestList.toTypedArray().toLongArray())
    newIntent.putExtra("WorstList", worstList.toTypedArray().toLongArray())


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

  val recordList = mutableListOf<Long>()
  val bestList = mutableListOf<Long>()
  val worstList = mutableListOf<Long>()
  private fun checkMarker() {
    if (nextWP == markerList.size) return

    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        markerList[nextWP].position
      ) < ARRIVE_BOUNDARY
    ) {
      val recordTimes = mapRouteGPX.wptList[nextWP].desc!!.split(",")
      val nowRecord = SystemClock.elapsedRealtime() - runningTimerTextView.base
      bestList.add(recordTimes.first().toLong())
      worstList.add(recordTimes.last().toLong())
      val a = nowRecord.calcRank(bestList.last(), worstList.last())
      recordList.add(nowRecord)
      TTS.speech("you are in ${a} % now")
      traceMap.changeMarkerIcon(nextWP)
      nextWP++
      wpList.add(currentLocation.toWayPoint(DISTANCE_POINT))
      if (nextWP == markerList.size) {
        stop(getString(R.string.finishRacing))
      }
    }
  }

  private fun checkDeviation() {
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
