package com.korea50k.tracer.racing

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.NearMap
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.activity_near_route.*
import kotlinx.android.synthetic.main.recycler_nearactivity_item.*


class NearRouteActivity : AppCompatActivity() {

    lateinit var racerData: InfoData
    lateinit var makerData: InfoData

    lateinit var locationCallback: LocationCallback
    var cur_loc = LatLng(0.0, 0.0)          //현재위치
    var nearMaps: ArrayList<NearMap> = arrayListOf()

    lateinit var distanceArray: ArrayList<Double>
    lateinit var mapTitleArray: ArrayList<String>

    var latLng = LatLng(0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_route)
        val progressbar = ProgressBar(this)
        progressbar.show()

        //startFragment에서 넘긴 현재 위치를 intent로 받음
        val intent = getIntent()
        var current = intent.extras?.getParcelable<Location>("currentLocation")

        Log.d("jsj", "near 넘어와서 getintent 한 값 " + current.toString())

        //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
        //Location 형태로 받아오고 싶다면 아래처럼
        //var getintentLocation = current
        var lat = current!!.latitude
        var lng = current!!.longitude
        cur_loc = LatLng(lat, lng)

        Log.d("ssmm11", "현재 위치 = " + lat.toString() + lng.toString())


        val db = FirebaseFirestore.getInstance()

        db.collection("mapRoute")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var receiveRouteDatas = document.get("markerlatlngs") as List<Object>

                    for (receiveRouteData in receiveRouteDatas) {
                        val location = receiveRouteData as Map<String, Any>
                        latLng = LatLng(
                            location["latitude"] as Double,
                            location["longitude"] as Double
                        )
                        break
                    }
                    var nearMap = NearMap(document.id, SphericalUtil.computeDistanceBetween(cur_loc, latLng))
                    nearMaps.add(nearMap)
                }
                near_recycler_map.adapter = NearRecyclerViewAdapter(nearMaps.sortedWith(compareBy({it.distance})))
                near_recycler_map.layoutManager = LinearLayoutManager(this)
                progressbar.dismiss()
            }
            .addOnFailureListener { exception ->
                Log.w("ssmm11", "Error getting documents.", exception)
            }
    }
}
