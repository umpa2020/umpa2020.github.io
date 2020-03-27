package com.umpa2020.tracer.main.start.racing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.activity_near_route.*


class NearRouteActivity : AppCompatActivity() {

  lateinit var racerData: InfoData
  lateinit var makerData: InfoData

  lateinit var locationCallback: LocationCallback
  var cur_loc = LatLng(0.0, 0.0)          //현재위치
  var nearMaps: ArrayList<NearMap> = arrayListOf()
  var latLng = LatLng(0.0, 0.0)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_near_route)
    val progressbar = ProgressBar(this)
    progressbar.show()

    //startFragment에서 넘긴 현재 위치를 intent로 받음
    val intent = intent
    val cur_loc = intent.extras?.getParcelable<LatLng>("currentLocation")

    val db = FirebaseFirestore.getInstance()

    /**
     * mapInfo 에 저장된 맵의 첫 시작 위치와
     * 현재 위치의 거리를 구해서 20km 미만이면
     * 가까운 순서대로 정렬하여 구현한다.
     */
    db.collection("mapInfo")
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val infoData = document.toObject(InfoData::class.java)
          latLng = LatLng(
            infoData.startLatitude!!,
            infoData.startLongitude!!
          )

          if (SphericalUtil.computeDistanceBetween(cur_loc, latLng) <= 20000) {
            val nearMap = NearMap(document.id, SphericalUtil.computeDistanceBetween(cur_loc, latLng))
            nearMaps.add(nearMap)
          }
        }
        near_recycler_map.adapter = NearRecyclerViewAdapter(nearMaps.sortedWith(compareBy { it.distance }))
        near_recycler_map.layoutManager = LinearLayoutManager(this)
        progressbar.dismiss()
      }
  }
}
