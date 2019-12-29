package com.korea50k.RunShare.MainFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.Activities.Running.RunningActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.DataClass.Map
import kotlinx.android.synthetic.main.fragment_map.view.*

class MapFragment : Fragment(),View.OnClickListener{
    lateinit var map: Map

    override fun onClick(v: View) {
        Log.wtf("WTF","WTF")
        var newIntent = Intent(activity, RunningActivity::class.java)
        startActivity(newIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_map, container, false)

        val smf = childFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = Map(smf, context as Context)
        view.btn_start.setOnClickListener(this)

        return view
    }
}
