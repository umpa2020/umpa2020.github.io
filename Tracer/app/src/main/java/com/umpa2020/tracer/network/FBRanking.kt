package com.umpa2020.tracer.network

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.App
import com.umpa2020.tracer.constant.Constants.Companion.MAX_DISTANCE
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.main.ranking.RankRecyclerViewAdapterMap
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * 랭킹 네트워크 클래스 - 랭킹에 관련한
 * 네트워크 접근 함수는 이 곳에 정의
 */
class FBRanking {
  lateinit var infoData: InfoData
  lateinit var infoDatas: ArrayList<InfoData>
  var nearMaps1: ArrayList<NearMap> = arrayListOf()

  var cur_loc = LatLng(0.0, 0.0)          //현재위치

  val db = FirebaseFirestore.getInstance()

  /**
   * 필터를 거치지 않고, 실행순으로 정렬되는 데이터를 가져오는 함수.
   */

  fun getExcuteDESCENDING(context: Context, view: View, latlng: LatLng, mode: String) {
    val progressbar = ProgressBar(context)
    progressbar.show()
    cur_loc = latlng
    nearMaps1.clear()

    //레이아웃 매니저 추가
    view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

    db.collection("mapInfo")
      .get()
      .addOnSuccessListener { result ->
        infoDatas = arrayListOf()

        for (document in result) {
          infoData = document.toObject(InfoData::class.java)
          infoData.mapTitle = document.id

          infoData.distance = SphericalUtil.computeDistanceBetween(cur_loc, LatLng(infoData.startLatitude!!, infoData.startLongitude!!))
          infoDatas.add(infoData)
        }
        infoDatas.sortByDescending { infoData -> infoData.execute }
        if (infoDatas.isEmpty()) {
          view.rankingRecyclerRouteisEmpty.visibility = View.VISIBLE
          progressbar.dismiss()
        }
        view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas, mode, progressbar)
      }
  }

  /**
   * 현재 위치를 받아서 현재 위치와 필터에 적용한 위치 만큼 떨어져 있는 구간에서 실행순으로 정렬한 코드
   */

  fun getFilterRange(view: View, latlng: LatLng, distance: Int, mode: String) {
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()

    //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
    //Location 형태로 받아오고 싶다면 아래처럼
    //var getintentLocation = current
    cur_loc = latlng

    //레이아웃 매니저 추가
    view.rank_recycler_map.layoutManager = LinearLayoutManager(App.instance.currentActivity() as Activity)

    nearMaps1.clear()

    db.collection("mapInfo")
      .get()
      .addOnSuccessListener { result ->
        infoDatas = arrayListOf()

        for (document in result) {
          infoData = document.toObject(InfoData::class.java)
          infoData.mapTitle = document.id
          infoData.distance = SphericalUtil.computeDistanceBetween(cur_loc, LatLng(infoData.startLatitude!!, infoData.startLongitude!!))
          if (distance != MAX_DISTANCE) {
            if (infoData.distance!! < distance * 1000)
              infoDatas.add(infoData)
          } else {
            infoDatas.add(infoData)
          }
        }

        if (mode.equals("execute")) {
          infoDatas.sortByDescending { infoData -> infoData.execute }
        } else if (mode.equals("likes")) {
          infoDatas.sortByDescending { infoData -> infoData.likes }
        }
        if (infoDatas.isEmpty()) {
          view.rankingRecyclerRouteisEmpty.visibility = View.VISIBLE
          progressbar.dismiss()
        } else {
          view.rankingRecyclerRouteisEmpty.visibility = View.GONE
          progressbar.dismiss()
        }
        view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas, mode, progressbar)
      }
  }
}