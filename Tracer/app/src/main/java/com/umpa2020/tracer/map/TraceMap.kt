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
  val passedIcon = R.drawable.ic_checkpoint_red.makingIcon()

  var markerList = mutableListOf<Marker>()
  var turningPointList= mutableListOf<Marker>()
  fun drawRoute(track: MutableList<LatLng>, wptList: MutableList<WayPoint>) :MutableList<Marker>{
    Logg.d("Map is draw")
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    val unPassedIcon = R.drawable.ic_checkpoint_gray.makingIcon()
    wptList.forEachIndexed { i, it ->
       when (it.type.get()) {
        START_POINT -> {
          markerList.add(mMap.addMarker(
            MarkerOptions()
              .position(it.toLatLng())
              .title(it.name.get())
              .icon(R.drawable.ic_racing_startpoint.makingIcon())
          ))
        }
        FINISH_POINT -> {
          markerList.add(mMap.addMarker(
            MarkerOptions()
              .position(it.toLatLng())
              .title(it.name.get())
              .icon(
                R.drawable.ic_racing_finishpoint.makingIcon())
          ))
        }
        DISTANCE_POINT -> {
          markerList.add(mMap.addMarker(
            MarkerOptions()
              .position(it.toLatLng())
              .title(it.name.get())
              .icon(
                unPassedIcon)
          ))
        }
        TURNING_LEFT_POINT -> {
          turningPointList.add(mMap.addMarker(
            MarkerOptions()
              .position(it.toLatLng())
              .title(it.name.get())
          ))
        }
        TURNING_RIGHT_POINT -> {
          turningPointList.add(mMap.addMarker(
            MarkerOptions()
              .position(it.toLatLng())
              .title(it.name.get())
          ))
        }
        else -> {
          unPassedIcon
        }
      }
    }
    val trackBounds = track.bounds()
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trackBounds, 1080, 300, 100))
    return markerList
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
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17F))
  }

  fun changeMarkerIcon(nextWP: Int) {
    markerList[nextWP].remove()
    markerList[nextWP] = mMap.addMarker(
      MarkerOptions()
        .position(markerList[nextWP].position)
        .title(markerList[nextWP].title)
        .icon(passedIcon)
    )
  }
}