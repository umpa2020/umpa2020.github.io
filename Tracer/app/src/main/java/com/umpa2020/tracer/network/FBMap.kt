package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.util.Logg

class FBMap {
  val NEARMAPTRUE = 40
  val NEARMAPFALSE = 41
  val STRAT_FRAGMENT_NEARMAP = 30
  val db = FirebaseFirestore.getInstance()
  private var latLng = LatLng(0.0, 0.0)
  private var nearMaps: ArrayList<NearMap> = arrayListOf()

  fun getNearMap(southwest: LatLng, northeast: LatLng, mHandler: Handler) {


    // 경도선에 걸린 좌표 값

    db.collection("mapInfo")
      .whereGreaterThan("startLatitude", southwest.latitude)
      .whereLessThan("startLatitude", northeast.latitude)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val startLongitude = document.get("startLongitude") as Double
          val startLatitude = document.get("startLatitude") as Double
          if (southwest.longitude > 0 && northeast.longitude < 0) {
            if (southwest.longitude < startLongitude || startLongitude < northeast.longitude) {
              val nearMap = NearMap(document.id, LatLng(startLatitude, startLongitude), document.get("distance") as Double)
              nearMaps.add(nearMap)
            }
          }
          else if (southwest.longitude < startLongitude && startLongitude < northeast.longitude) {
            val nearMap = NearMap(document.id, LatLng(startLatitude, startLongitude), document.get("distance") as Double)
            nearMaps.add(nearMap)
          }
        }
        val msg: Message = if (nearMaps.isEmpty()) {
          mHandler.obtainMessage(NEARMAPFALSE)
        } else {
          mHandler.obtainMessage(STRAT_FRAGMENT_NEARMAP)
        }
        msg.obj = nearMaps
        Logg.d("ssmm11 msg.obj = ${msg.obj}")
        mHandler.sendMessage(msg)

      }


    /**
     * mapInfo 에 저장된 맵의 첫 시작 위치와
     * 현재 위치의 거리를 구해서 20km 미만이면
     * 가까운 순서대로 정렬하여 구현한다.
     */
    /*db.collection("mapInfo")
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val infoData = document.toObject(InfoData::class.java)
          latLng = LatLng(
            infoData.startLatitude!!,
            infoData.startLongitude!!
          )

          if (SphericalUtil.computeDistanceBetween(curLoc, latLng) <= 20000) {
            val nearMap = NearMap(document.id, SphericalUtil.computeDistanceBetween(curLoc, latLng))
            nearMaps.add(nearMap)
          }


        }
        val msg: Message = if (nearMaps.isEmpty()) {
          mHandler.obtainMessage(NEARMAPFALSE)
        } else {
          mHandler.obtainMessage(NEARMAPTRUE)
        }
        msg.obj = nearMaps
        Logg.d("ssmm11 msg.obj = ${msg.obj}")
        mHandler.sendMessage(msg)
      }*/
  }
}