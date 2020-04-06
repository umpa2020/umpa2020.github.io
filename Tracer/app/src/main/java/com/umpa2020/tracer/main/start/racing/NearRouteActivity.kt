package com.umpa2020.tracer.main.start.racing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.NearMap


class NearRouteActivity : AppCompatActivity() {
  var nearMaps: ArrayList<NearMap> = arrayListOf()
  val NEARMAPTRUE = 40
  val NEARMAPFALSE = 41

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_near_route)
 /*   val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()

    //startFragment에서 넘긴 현재 위치를 intent로 받음
    val intent = intent
    val curLoc = intent.extras?.getParcelable<LatLng>("currentLocation")


    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          NEARMAPTRUE -> {
            nearMaps = msg.obj as ArrayList<NearMap>
            Logg.d("ssmm11 nearMaps = $nearMaps")

            //adpater 추가
            near_recycler_map.adapter = NearRecyclerViewAdapter(nearMaps.sortedWith(compareBy { it.distance }), progressbar)
            near_recycler_map.layoutManager = LinearLayoutManager(baseContext)

            NearRouteisEmpty.visibility = View.GONE
          }
          NEARMAPFALSE -> {
            NearRouteisEmpty.visibility = View.VISIBLE
          }
        }
      }
    }
    FBMap().getNearMap(curLoc!!, mHandler)*/
  }
}
