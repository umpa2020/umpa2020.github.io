package com.umpa2020.tracer.map

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.getMinMax
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.Wow
import io.jenetics.jpx.WayPoint

class TraceMap(val mMap: GoogleMap) {

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  lateinit var loadTrack: Polyline
  val passedIcon=Wow.makingIcon(R.drawable.ic_checkpoint_red,App.instance.context())
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
    val unPassedIcon=Wow.makingIcon(R.drawable.ic_checkpoint_gray,App.instance.context())
    wptList.forEachIndexed { i, it ->
      val icon = when (i) {
        0 -> {
          Wow.makingIcon(R.drawable.ic_racing_startpoint, App.instance.context())
        }
        wptList.size - 1 -> {
          Wow.makingIcon(R.drawable.ic_racing_finishpoint,App.instance.context())
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
    val trackMinMax = track.getMinMax()
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(trackMinMax.first, trackMinMax.second), 1080, 300, 100))
    Logg.d(trackMinMax.first.toString() + trackMinMax.second.toString())
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