package com.umpa2020.tracer.main.trace

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.testdata
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.main.trace.racing.NearRouteActivity
import com.umpa2020.tracer.main.trace.running.RunningActivity
import com.umpa2020.tracer.map.BasicMap
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_start.view.*


class StartFragment : Fragment(), View.OnClickListener {
  val WSY = "StartFragment"

  lateinit var map: BasicMap
  var mHandler: IncomingMessageHandler? = null

  lateinit var curLoc: Location

  var smf: SupportMapFragment? = null

  var lastLacation: Location? = null

  override fun onClick(v: View) {
    when (v.id) {

      R.id.mainStartRunning -> {
        val newIntent = Intent(activity, RunningActivity::class.java)
        startActivity(newIntent)
      }

      R.id.mainStartRacing -> {
        val newIntent = Intent(activity, NearRouteActivity::class.java)
        newIntent.putExtra("currentLocation", curLoc) //curLoc 정보 인텐트로 넘김
        Log.d("jsj", "mainStartRunning누르는 순간의 intent " + curLoc.toString())
        startActivity(newIntent)
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    Log.d(WSY, "onCreateView()")
    val view = inflater.inflate(R.layout.fragment_start, container, false)
    view.test.setOnClickListener {
      val testdata = testdata()
      val db = FirebaseFirestore.getInstance()
      db.collection("mapRoute").document("테스트트트트트트").set(testdata)
        .addOnSuccessListener {
          Log.d("ssmm11", "성공!")
        }
        .addOnFailureListener {
        }
    }

    return view
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(WSY, "onViewCreated()")
    smf = childFragmentManager.findFragmentById(com.umpa2020.tracer.R.id.map_viewer_start) as SupportMapFragment
    map = BasicMap(smf!!, context as Context)

    view.mainStartRunning.setOnClickListener(this)
    view.mainStartRacing.setOnClickListener(this)
  }

  override fun onPause() {
    super.onPause()
    Log.d("sss", "onPause()")
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d("sss", "onDestroy()")
  }

  override fun onResume() {
    super.onResume()
    mHandler = IncomingMessageHandler()

    Intent(context, LocationBackgroundService::class.java).also {
      val messengerIncoming = Messenger(mHandler)
      it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
      activity!!.startService(it)
    }
  }

  val MESSENGER_INTENT_KEY = "msg-intent-key"

  // 옵저버 패턴에서 location Manager
  inner class IncomingMessageHandler : Handler() {
    override fun handleMessage(msg: Message) {
      super.handleMessage(msg)
      when (msg.what) {
        LocationBackgroundService.LOCATION_MESSAGE -> {
          curLoc = msg.obj as Location
          Log.d(WSY, "StartFragment : $curLoc")
          map.setLocation(curLoc)
          UserInfo.rankingLatitude = curLoc.latitude
          UserInfo.rankingLongitude = curLoc.longitude
          if (curLoc != null)
            lastLacation = curLoc
        }
      }
    }
  }
}
