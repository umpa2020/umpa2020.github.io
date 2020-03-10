package com.umpa2020.tracer.network

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.ranking.RankRecyclerViewAdapterMap
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.fragment_ranking.view.*
import java.util.*


/**
 * 랭킹 네트워크 클래스 - 랭킹에 관련한
 * 네트워크 접근 함수는 이 곳에 정의
 */
class getRanking {
    lateinit var infoData: InfoData
    lateinit var infoDatas: ArrayList<InfoData>


    var cur_loc = LatLng(0.0, 0.0)          //현재위치
    var latLng = LatLng(0.0,0.0)



    /**
     * 필터를 거치지 않고, 실행순으로 정렬되는 데이터를 가져오는 함수.
     */

    fun getExcuteDESCENDING(context: Context, view: View) {
        val progressbar = ProgressBar(context)
        progressbar.show()

        //레이아웃 매니저 추가
        view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

        val db = FirebaseFirestore.getInstance()

        db.collection("mapInfo").orderBy("execute", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                infoDatas = arrayListOf()

                for (document in result) {
                    infoData = document.toObject(InfoData::class.java)
                    infoData.mapTitle = document.id
                    infoDatas.add(infoData)
                }
                view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas)

                progressbar.dismiss()
            }
            .addOnFailureListener { exception ->
                Log.d("ssmm11", exception.toString())

            }
    }

    fun getFilterRange(context: Context, view: View, location: Location) {
        val progressbar = ProgressBar(context)
        progressbar.show()

        //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
        //Location 형태로 받아오고 싶다면 아래처럼
        //var getintentLocation = current
        val lat = location.latitude
        val lng = location.longitude
        cur_loc = LatLng(lat, lng)

        //레이아웃 매니저 추가
        view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

        val db = FirebaseFirestore.getInstance()

        db.collection("mapRoute")
            .get()
            .addOnSuccessListener { result ->
                infoDatas = arrayListOf()

                //TODO: SORT 함수를 새로 만들어서 해야할 듯 가져오는 건 되는데 정렬이 안되니깐
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
                    if ( SphericalUtil.computeDistanceBetween(cur_loc, latLng) <= 22000) {
                        db.collection("mapInfo").whereEqualTo("mapTitle", document.id)
                            .get()
                            .addOnSuccessListener { result2 ->

                                for (document2 in result2) {
                                    infoData = document2.toObject(InfoData::class.java)
                                    Log.d("ssmm11", "맵 : " + document2.id + " 실행수 : "+ infoData.execute)
                                    infoData.mapTitle = document2.id
                                    infoDatas.add(infoData)
                                    break
                                }

                                infoDatas.sortByDescending { infoData -> infoData.execute  }
                                view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas)

                                progressbar.dismiss()
                            }
                    }
                }


                progressbar.dismiss()
            }
            .addOnFailureListener { exception ->
                Log.w("ssmm11", "Error getting documents.", exception)
            }
    }

}