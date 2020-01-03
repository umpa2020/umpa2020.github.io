package com.korea50k.RunShare.MainFragment

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.content.res.AssetManager.AssetInputStream
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.RunShare.Activities.Racing.RacingActivity
import com.korea50k.RunShare.Activities.Running.RunningActivity
import com.korea50k.RunShare.DataClass.ConvertJson
import com.korea50k.RunShare.DataClass.Map
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.io.IOException


class MapFragment : Fragment(),View.OnClickListener{
    lateinit var map: Map

    override fun onClick(v: View) {
        when(v.id){
            R.id.start_btn->{
                var newIntent = Intent(activity, RunningActivity::class.java)
                startActivity(newIntent)

            }
            R.id.race_btn->{

                val assetManager = resources.assets

                //TODO:서버에서 데이터 가져와서 해야함
                val inputStream= assetManager.open("testjson")
                val jsonString = inputStream.bufferedReader().use { it.readText() }


                var newIntent = Intent(activity, RacingActivity::class.java)
                newIntent.putExtra("Running data",ConvertJson.JsonToRunningData(jsonString))
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
        var view = inflater.inflate(R.layout.fragment_map, container, false)

        val smf = childFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = Map(smf, context as Context)
        view.start_btn.setOnClickListener(this)
        view.race_btn.setOnClickListener(this)

        return view
    }
}
