package com.umpa2020.tracer.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RacerData
import com.umpa2020.tracer.extensions.bounds
import com.umpa2020.tracer.extensions.makingIcon
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.util.GlideApp
import kotlinx.android.synthetic.main.profile_marker.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TraceMap(val mMap: GoogleMap) {

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  lateinit var loadTrack: Polyline
  val passedIcon = R.drawable.ic_passed_circle.makingIcon()
  var markerList = mutableListOf<Marker>()
  var turningPointList = mutableListOf<Marker>()
  val unPassedIcon = R.drawable.ic_unpassed_circle.makingIcon()
  var mapDownFlag = false
  lateinit var trackBounds : LatLngBounds
  fun drawRoute(
    trkList: List<WayPoint>,
    wptList: List<WayPoint>
  ): Pair<MutableList<Marker>, MutableList<Marker>> {


    val track = trkList.map { it.toLatLng() }
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합

    wptList.forEach { addMarker(it) }
    trackBounds = track.toMutableList().bounds()

    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trackBounds, 1080, 300, 20))
    mapDownFlag = true
    return Pair(markerList, turningPointList)
  }

  var polyFlag = true
  lateinit var poly: Polyline
  fun drawPolyLine(preLoc: LatLng, curLoc: LatLng) {


    if (polyFlag) {
      //polyline 그리기
      poly = mMap.addPolyline(
        PolylineOptions().add(
          preLoc,
          curLoc
        )
      )
      polyFlag = false
    } else {
      val a = poly.points
      a.add(curLoc)
      poly.points = a

    }
  }

  fun moveCameraUserDirection(curLoc: Location, zoomLevel: Float) {

    mMap.animateCamera(
      CameraUpdateFactory.newCameraPosition(
        CameraPosition(curLoc.toLatLng()/*좌표*/, zoomLevel/*줌 레벨*/, 0F/*기울기 각도*/, curLoc.bearing/*베어링 각도*/)
      )
    )
  }

  fun moveCamera(latlng: LatLng, zoomLevel: Float) {
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel))
  }

  fun changeMarkerIcon(nextWP: Int) {
    markerList[nextWP].setIcon(passedIcon)
  }

  fun initCamera(latlng: LatLng) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16F))
  }

  var racerList = mutableListOf<Marker>()

  fun updateMarker(i: Int, latlng: LatLng) {
    racerList[i].position = latlng
  }

  suspend fun addRacer(latlng: LatLng, racerData: RacerData, mCustomMarkerView: View) {

    racerList.add(
      mMap.addMarker(
        MarkerOptions()
          .position(latlng)
          .title(racerData.racerName)
          .icon(
            makeProfileIcon(mCustomMarkerView, racerData.racerId)
          )
          .draggable(true)
      )
    )

  }

  suspend fun makeProfileIcon(view: View, uid: String): BitmapDescriptor {
    val drawable = withContext(Dispatchers.IO) {
      GlideApp.with(App.instance.context())
        .load(FBProfileRepository().getProfile(uid).imgPath)
        .override(1024, 980)
        .error(R.drawable.logosquare)
        .dontAnimate()
        .dontTransform().submit()
        .get()
    }
    view.profile_image.setImageDrawable(drawable)
    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = Bitmap.createBitmap(
      view.measuredWidth, view.measuredHeight,
      Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
    view.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
  }

  fun removeRacer(racerNo: Int) {
    racerList[racerNo].remove()
  }

  fun addMarker(wpt: WayPoint) {
    when (wpt.type) {
      START_POINT -> {
        markerList.add(
          mMap.addMarker(
            MarkerOptions()
              .position(wpt.toLatLng())
              .title(wpt.name)
              .icon(R.drawable.ic_start_point.makingIcon())
          )
        )
      }
      FINISH_POINT -> {
        markerList.add(
          mMap.addMarker(
            MarkerOptions()
              .position(wpt.toLatLng())
              .title(wpt.name)
              .icon(R.drawable.ic_finish_point.makingIcon())
          )
        )
      }
      DISTANCE_POINT -> {
        markerList.add(
          mMap.addMarker(
            MarkerOptions()
              .position(wpt.toLatLng())
              .title(wpt.name)
              .icon(unPassedIcon)
              .anchor(0f, 0.5f)
          )
        )
      }
      TURNING_LEFT_POINT -> {
        turningPointList.add(
          mMap.addMarker(
            MarkerOptions()
              .position(wpt.toLatLng())
              .title(wpt.desc)
              .icon(R.drawable.ic_turn_left.makingIcon())
          )
        )
      }
      TURNING_RIGHT_POINT -> {
        turningPointList.add(
          mMap.addMarker(
            MarkerOptions()
              .position(wpt.toLatLng())
              .title(wpt.desc)
              .icon(R.drawable.ic_turn_right.makingIcon())
          )
        )
      }
      else -> {
      }
    }
  }
}
