package com.umpa2020.tracer.map

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.bounds
import com.umpa2020.tracer.extensions.makingIcon
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

class TraceMap(val mMap: GoogleMap) {

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  lateinit var loadTrack: Polyline
  val passedIcon=R.drawable.ic_checkpoint_red.makingIcon()

  var markerList = mutableListOf<Marker>()
  fun drawRoute(track: MutableList<LatLng>, wptList: MutableList<WayPoint>) {
    Logg.d("Map is draw")
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    val unPassedIcon=R.drawable.ic_checkpoint_gray.makingIcon()
    wptList.forEachIndexed { i, it ->
      val icon = when (i) {
        0 -> {
          R.drawable.ic_racing_startpoint.makingIcon()
        }
        wptList.size - 1 -> {
          R.drawable.ic_racing_finishpoint.makingIcon()
        }
        else -> {
          unPassedIcon
        }
      }

      markerList.add(
        mMap.addMarker(
          MarkerOptions()
            .position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
            .title(it.name.toString())
            .icon(icon)
        )
      )
    }
    val trackBounds = track.bounds()
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trackBounds, 1080, 300, 100))
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

  fun changeMarkerColor(nextWP: Int,color:Float) {
    markerList[nextWP].remove()
    markerList[nextWP] = mMap.addMarker(
      MarkerOptions()
        .position(markerList[nextWP].position)
        .title(markerList[nextWP].title)
        .icon(passedIcon)
    )
  }
}