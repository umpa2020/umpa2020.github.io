package com.umpa2020.tracer.main.start

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.trace
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.testdata
import com.umpa2020.tracer.trace.decorate.BasicMap
import com.umpa2020.tracer.main.start.racing.NearRouteActivity
import com.umpa2020.tracer.main.start.racing.RankingRecodeRacingActivity
import com.umpa2020.tracer.main.start.running.RunningActivity
import com.umpa2020.tracer.trace.decorate.TraceMap
import com.umpa2020.tracer.util.LocationBroadcastReceiver
import com.umpa2020.tracer.util.UserInfo
import com.umpa2020.tracer.util.gpx.GPXConverter
import io.jenetics.jpx.GPX
import kotlinx.android.synthetic.main.fragment_start.view.*
import java.io.File
import java.nio.file.Path


class StartFragment : Fragment(), View.OnClickListener {
    val TAG = "StartFragment"

    lateinit var traceMap: TraceMap
//    var mHandler: IncomingMessageHandler? = null

    lateinit var currentLocation: Location

    lateinit var locationBroadcastReceiver:LocationBroadcastReceiver

    override fun onClick(v: View) {
        when (v.id) {

            R.id.mainStartRunning -> {
                val newIntent = Intent(activity, RunningActivity::class.java)
                startActivity(newIntent)
            }

            R.id.mainStartRacing -> {
                val newIntent = Intent(activity, NearRouteActivity::class.java)
                newIntent.putExtra("currentLocation", currentLocation) //curLoc 정보 인텐트로 넘김
                Log.d("jsj", "mainStartRunning누르는 순간의 intent " + currentLocation.toString())
                startActivity(newIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView()")
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        view.test.setOnClickListener {
            Log.d("ssmm11", "test 실행")
            val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
            val routeRef = storage.reference.child("mapRoute").child("Sandiego||1585155309000")
            val localFile = File.createTempFile("routeGpx", "xml")

            routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
                var routeGPX=GPXConverter().GpxToClass(localFile.path)
                val intent=Intent(context,RankingRecodeRacingActivity::class.java)
                intent.putExtra("RouteGPX",routeGPX)
                startActivity(intent)
            }
            routeRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                } else {
                }
            }
        }
        val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
        traceMap = BasicMap(smf!!, context!!)
        locationBroadcastReceiver=LocationBroadcastReceiver(traceMap)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated()")


        view.mainStartRunning.setOnClickListener(this)
        view.mainStartRacing.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        // 브로드 캐스트 등록 - 전역 context로 수정해야함
        LocalBroadcastManager.getInstance(this.requireContext())
            .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))
    }

    override fun onPause() {
        super.onPause()
        Log.d("sss", "onPause()")

        //        브로드 캐스트 해제 - 전역 context로 수정해야함
        LocalBroadcastManager.getInstance(this.requireContext()).unregisterReceiver(locationBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("sss", "onDestroy()")
    }

}
