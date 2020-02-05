package com.korea50k.tracer.start

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.tracer.locationBackground.LocationBackgroundService
import com.korea50k.tracer.R
import com.korea50k.tracer.map.BasicMap
import kotlinx.android.synthetic.main.fragment_start.view.*
import java.text.DateFormat
import java.util.*


class StartFragment : Fragment(), View.OnClickListener {
    val WSY = "WSY"
    lateinit var map: BasicMap
    var mHandler : IncomingMessageHandler? = null

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mainStartRunning -> {
                val newIntent = Intent(activity, RunningActivity::class.java)
                startActivity(newIntent)
            }
            R.id.mainStartRacing -> {
                val newIntent = Intent(activity, NearRouteActivity::class.java)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment

        map = BasicMap(smf, context as Context)
        view.mainStartRunning.setOnClickListener(this)
        view.mainStartRacing.setOnClickListener(this)
    }


    val MESSENGER_INTENT_KEY = "msg-intent-key"

    inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(WSY, "handleMessage..." + msg.toString())

            super.handleMessage(msg)

            when (msg.what) {
                LocationBackgroundService.LOCATION_MESSAGE -> {
                    val obj = msg.obj as Location
                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                    Log.d(WSY,"StartFragment : 값을 가져옴?")
                    map.setLocation(obj)
                }
            }
        }
    }
}
