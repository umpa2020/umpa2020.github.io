package com.umpa2020.tracer.map

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.DISTANCE_POINT
import com.umpa2020.tracer.constant.Constants.Companion.FINISH_POINT
import com.umpa2020.tracer.constant.Constants.Companion.START_POINT
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_LEFT_POINT
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_RIGHT_POINT
import com.umpa2020.tracer.extensions.bounds
import com.umpa2020.tracer.extensions.makingIcon
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

class TraceMap(val mMap: GoogleMap) {

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  lateinit var loadTrack: Polyline
  val passedIcon = R.drawable.ic_passed_circle.makingIcon()

  var markerList = mutableListOf<Marker>()
  var turningPointList = mutableListOf<Marker>()
  fun drawRoute(
    trkList: MutableList<WayPoint>,
    wptList: MutableList<WayPoint>
  ): Pair<MutableList<Marker>, MutableList<Marker>> {
    Logg.d("Map is draw")
    val track=trkList.map{it.toLatLng()}
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    val unPassedIcon = R.drawable.ic_unpassed_circle.makingIcon()
    wptList.forEachIndexed { i, it ->
      when (it.type.get()) {
        START_POINT -> {
          markerList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.name.get())
                .icon(R.drawable.ic_start_point.makingIcon())
            )
          )

        }
        FINISH_POINT -> {
          markerList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.name.get())
                .icon(R.drawable.ic_finish_point.makingIcon())
            )
          )
        }
        DISTANCE_POINT -> {
          markerList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.name.get())
                .icon(unPassedIcon)
                .anchor(0f,0.5f)
            )
          )
        }
        TURNING_LEFT_POINT -> {
          turningPointList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.description.get())
                .icon(R.drawable.ic_turn_left.makingIcon())
            )
          )
        }
        TURNING_RIGHT_POINT -> {
          turningPointList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.description.get())
                .icon(R.drawable.ic_turn_right.makingIcon())
            )
          )
        }
        else -> { }
      }
    }
    val trackBounds = track.toMutableList().bounds()
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trackBounds, 1080, 300, 100))
    return Pair(markerList, turningPointList)
  }

  fun drawPolyLine(preLoc: LatLng, curLoc: LatLng) {
    Logg.d("making polyline $preLoc $curLoc")

    //polyline 그리기
    mMap.addPolyline(
      PolylineOptions().add(
        preLoc,
        curLoc
      )
    )
  }

  fun moveCamera(latlng: LatLng) {
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
  }

  fun changeMarkerIcon(nextWP: Int) {
    markerList[nextWP].remove()
    markerList[nextWP] = mMap.addMarker(
      MarkerOptions()
        .position(markerList[nextWP].position)
        .title(markerList[nextWP].title)
        .icon(passedIcon)
        .anchor(0f,0.5f)
    )
  }

  fun initCamera(latlng: LatLng) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,17F))
  }
}