package com.umpa2020.tracer.network

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.App
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.main.ranking.RankRecyclerViewAdapterMap
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * 랭킹 네트워크 클래스 - 랭킹에 관련한
 * 네트워크 접근 함수는 이 곳에 정의
 */
class GetRanking {
  lateinit var infoData: InfoData
  lateinit var infoDatas: ArrayList<InfoData>
  var nearMaps1: ArrayList<NearMap> = arrayListOf()

  var cur_loc = LatLng(0.0, 0.0)          //현재위치
  var latLng = LatLng(0.0, 0.0)

  /**
   * 필터를 거치지 않고, 실행순으로 정렬되는 데이터를 가져오는 함수.
   */

  fun getExcuteDESCENDING(context: Context, view: View, lat: Double, lng: Double, mode: String) {
    val progressbar = ProgressBar(context)
    progressbar.show()
    //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
    //Location 형태로 받아오고 싶다면 아래처럼
    //var getintentLocation = current

    cur_loc = LatLng(lat, lng)

    nearMaps1.clear()

    //레이아웃 매니저 추가
    view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

    val db = FirebaseFirestore.getInstance()

    db.collection("mapRoute")
      .get()
      .addOnSuccessListener { result ->
        infoDatas = arrayListOf()

        for (document in result) {
          val receiveRouteDatas = document.get("markerlatlngs") as List<Object>

          for (receiveRouteData in receiveRouteDatas) {
            val location = receiveRouteData as Map<String, Any>
            latLng = LatLng(
              location["latitude"] as Double,
              location["longitude"] as Double
            )
            nearMaps1.add(NearMap(document.id, SphericalUtil.computeDistanceBetween(cur_loc, latLng)))
            break
          }
        }

        for (nearmap in nearMaps1) {
          db.collection("mapInfo").whereEqualTo("mapTitle", nearmap.mapTitle)
            .get()
            .addOnSuccessListener { result2 ->

              for (document2 in result2) {
                infoData = document2.toObject(InfoData::class.java)
                infoData.mapTitle = document2.id
                infoData.distance = nearmap.distance
                infoDatas.add(infoData)
                break
              }
              infoDatas.sortByDescending { infoData -> infoData.execute }
              view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas, mode, progressbar)
            }
        }
      }
      .addOnFailureListener { exception ->
      }
  }

  /**
   * 현재 위치를 받아서 현재 위치와 필터에 적용한 위치 만큼 떨어져 있는 구간에서 실행순으로 정렬한 코드
   */

  fun getFilterRange(view: View, lat: Double, lng: Double, distance: Int, mode: String) {
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()

    //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
    //Location 형태로 받아오고 싶다면 아래처럼
    //var getintentLocation = current
    cur_loc = LatLng(lat, lng)

    //레이아웃 매니저 추가
    view.rank_recycler_map.layoutManager = LinearLayoutManager(App.instance.currentActivity() as Activity)

    val db = FirebaseFirestore.getInstance()

    nearMaps1.clear()

    db.collection("mapRoute")
      .get()
      .addOnSuccessListener { result ->
        infoDatas = arrayListOf()

        for (document in result) {
          val receiveRouteDatas = document.get("markerlatlngs") as List<Object>

          for (receiveRouteData in receiveRouteDatas) {
            val location = receiveRouteData as Map<String, Any>
            latLng = LatLng(
              location["latitude"] as Double,
              location["longitude"] as Double
            )
            nearMaps1.add(NearMap(document.id, SphericalUtil.computeDistanceBetween(cur_loc, latLng)))
            break
          }
        }

        for (nearmap in nearMaps1) {
          if (nearmap.distance < distance * 1000) {
            db.collection("mapInfo").whereEqualTo("mapTitle", nearmap.mapTitle)
              .get()
              .addOnSuccessListener { result2 ->

                for (document2 in result2) {
                  infoData = document2.toObject(InfoData::class.java)
                  infoData.mapTitle = document2.id
                  infoData.distance = nearmap.distance
                  infoDatas.add(infoData)
                  break
                }
                if (mode.equals("execute")) {
                  infoDatas.sortByDescending { infoData -> infoData.execute }
                } else if (mode.equals("likes")) {
                  infoDatas.sortByDescending { infoData -> infoData.likes }
                }
                view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas, mode, progressbar)
              }
          }
        }
      }
      .addOnFailureListener { exception ->
      }
  }
}