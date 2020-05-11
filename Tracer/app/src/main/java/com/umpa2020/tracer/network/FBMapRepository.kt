package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.network.BaseFB.Companion.DISTANCE
import com.umpa2020.tracer.network.BaseFB.Companion.PLAYS
import com.umpa2020.tracer.network.BaseFB.Companion.MAPS
import com.umpa2020.tracer.network.BaseFB.Companion.START_LATITUDE
import com.umpa2020.tracer.network.BaseFB.Companion.START_LONGITUDE
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.tasks.await

class FBMapRepository : BaseFB() {
  val NEARMAPFALSE = 41
  val STRAT_FRAGMENT_NEARMAP = 30
  private var nearMaps: ArrayList<NearMap> = arrayListOf()

  suspend fun getMapTitle(mapId: String): String? {
    return db.collection(MAPS)
      .whereEqualTo(MAP_ID, mapId)
      .get()
      .await().documents.first().getString(MAP_TITLE)
  }

  fun listNearMap(southwest: LatLng, northeast: LatLng, mHandler: Handler) {
    // 경도선에 걸린 좌표 값

    db.collection(MAPS)
      .whereGreaterThan(START_LATITUDE, southwest.latitude)
      .whereLessThan(START_LATITUDE, northeast.latitude)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val startLongitude = document.get(START_LONGITUDE) as Double
          val startLatitude = document.get(START_LATITUDE) as Double
          if (southwest.longitude > 0 && northeast.longitude < 0) {
            if (southwest.longitude < startLongitude || startLongitude < northeast.longitude) {
              val nearMap = NearMap(
                document.id,
                LatLng(startLatitude, startLongitude),
                document.get(DISTANCE) as Double
              )
              nearMaps.add(nearMap)
            }
          } else if (southwest.longitude < startLongitude && startLongitude < northeast.longitude) {
            val nearMap = NearMap(
              document.id,
              LatLng(startLatitude, startLongitude),
              document.get(DISTANCE) as Double
            )
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

  fun updateExecute(mapTitle: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection(MAPS).document(mapTitle)
      .update(PLAYS, FieldValue.increment(1))
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully updated!") }
      .addOnFailureListener { e -> Logg.w("Error updating document$e") }
  }
}