package com.umpa2020.tracer.trace.decorate

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.WayPoint

interface TraceMap {
  fun work(location: Location)
  fun draw() {
    Logg.d("Map is draw")
    track = mutableListOf<LatLng>()
    routeGPX!!.trkList.forEach {
      track.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
    }
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    routeGPX!!.wptList.forEachIndexed { i, it ->
      val icon = when (i) {
        0 -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        }
        routeGPX!!.wptList.size - 1 -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
        else -> {
          BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

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
    val pair=getMinMax(track)
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(pair.first,pair.second), 1080, 300, 100))
      Logg.d(pair.first.toString()+pair.second.toString())
  }

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }


  fun start() {
    userState = UserState.RUNNING
  }

  fun pause() {
    privacy = Privacy.PUBLIC
    userState = UserState.PAUSED
  }

  fun restart() {
    userState = UserState.RUNNING
  }

  fun stop(record:Long): RouteGPX {
    userState = UserState.STOP
    work(Location(""))
  return RouteGPX(record.toString(), "", wpList, trkList)
  }

  fun WayPoint.toLatLng(): LatLng {
    return LatLng(latitude.toDouble(), longitude.toDouble())
  }

  fun getMinMax(latlngList: MutableList<LatLng>): Pair<LatLng, LatLng> {
    var minlat = track.first().latitude
    var maxlat = track.first().latitude
    var minlon = track.first().longitude
    var maxlon = track.first().longitude
    track.forEach {
      if (it.latitude < minlat) minlat = it.latitude
      else if (it.latitude > maxlat) maxlat = it.latitude
      if (it.longitude < minlat) minlon = it.longitude
      else if (it.longitude > maxlat) maxlon = it.longitude
    }
    return Pair(LatLng(minlat,minlon), LatLng(maxlat,maxlon))
  }

  var mMap: GoogleMap
  var testString: String
  var TAG: String      //로그용 태그
  var privacy: Privacy
  var distance: Double
  var time: Double
  var previousLocation: LatLng          //이전위치
  var currentLocation: LatLng           //현재위치
  var elevation: Double
  var speed: Double
  var userState: UserState     //사용자의 현재상태 달리기전 or 달리는중 등 자세한내용은 enum참고
  var moving: Boolean      //사용자가 현재 움직이는 중인지
  var trkList: MutableList<WayPoint>   //track point list
  var wpList: MutableList<WayPoint>   //way point list

  //routeGPX에서 로드한 정보들(racing용)
  var routeGPX: RouteGPX?
  var loadTrack: Polyline
  var markerList: MutableList<Marker>
  var track: MutableList<LatLng>
  var nextWP: Int
}