package com.korea50k.tracer.Start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.tracer.R
import com.korea50k.tracer.map.BasicMap
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*


class StartFragment : Fragment(), View.OnClickListener {
    lateinit var map: BasicMap
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start_btn -> {
                var newIntent = Intent(activity, RunningActivity::class.java)
                startActivity(newIntent)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val smf =
            childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
        //map = BasicMap(smf, context as Context)
        view.start_btn.setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()
//        map.pauseTracking()
    }

    override fun onResume() {
        super.onResume()
//        map.restartTracking()
    }
}
