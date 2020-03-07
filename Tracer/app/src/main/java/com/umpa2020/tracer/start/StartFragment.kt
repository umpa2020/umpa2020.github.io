package com.umpa2020.tracer.start

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.SupportMapFragment
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.map.BasicMap
import com.umpa2020.tracer.racing.NearRouteActivity
import com.umpa2020.tracer.util.LocationUpdatesComponent
import kotlinx.android.synthetic.main.fragment_start.view.*


class StartFragment : Fragment(), View.OnClickListener {
    val WSY = "StartFragment"
    lateinit var map: BasicMap
    var mHandler: IncomingMessageHandler? = null

    lateinit var obj: Location
    var smf: SupportMapFragment? = null

    override fun onClick(v: View) {
        when (v.id) {
            com.umpa2020.tracer.R.id.mainStartRunning -> {
                val newIntent = Intent(activity, RunningActivity::class.java)
                startActivity(newIntent)
            }
            com.umpa2020.tracer.R.id.mainStartRacing -> {
                val newIntent = Intent(activity, NearRouteActivity::class.java)
                newIntent.putExtra("currentLocation", obj) //obj 정보 인텐트로 넘김
                Log.d("jsj", "mainStartRunning누르는 순간의 intent " + obj.toString())
                startActivity(newIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHandler = IncomingMessageHandler()
        Log.d(WSY, "핸들러 생성?")
        Intent(context, LocationBackgroundService::class.java).also {
            val messengerIncoming = Messenger(mHandler)
            it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)

            activity!!.startService(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(WSY, "onCreateView()")
        return inflater.inflate(com.umpa2020.tracer.R.layout.fragment_start, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(WSY, "onViewCreated()")
        smf = childFragmentManager.findFragmentById(com.umpa2020.tracer.R.id.map_viewer_start) as SupportMapFragment
        map = BasicMap(smf!!, context as Context)
//        val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
//
//        map = BasicMap(smf, context as Context)
        view.mainStartRunning.setOnClickListener(this)
        view.mainStartRacing.setOnClickListener(this)
    }

    val MESSENGER_INTENT_KEY = "msg-intent-key"

    // 옵저버 패턴에서 location Manager
    inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(WSY, "handleMessage..." + msg.toString())

            super.handleMessage(msg)

            when (msg.what) {
                LocationBackgroundService.LOCATION_MESSAGE -> {
                    obj = msg.obj as Location
                    Log.d(WSY, "StartFragment : 값을 가져왔음")
                    Log.d(WSY, "현재 위치 : " + obj.toString())


                    map.setLocation(obj)
                }
            }
        }
    }
}
