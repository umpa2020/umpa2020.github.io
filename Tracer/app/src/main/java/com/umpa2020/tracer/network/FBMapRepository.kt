package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.util.Logg

class FBMapRepository {
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
          } else if (southwest.longitude < startLongitude && startLongitude < northeast.longitude) {
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
        mHandler.sendMessage(msg)

      }
  }

  fun increaseExecute(mapTitle: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("mapInfo").document(mapTitle)
      .update("execute", FieldValue.increment(1))
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully updated!") }
      .addOnFailureListener { e -> Logg.w("Error updating document$e") }
  }
}