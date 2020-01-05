package com.korea50k.RunShare.Activities.RankFragment


import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.Rank_MapData
import kotlinx.android.synthetic.main.fragment_rank_map.*
import kotlinx.android.synthetic.main.fragment_rank_map.view.*
import org.json.JSONObject

class fragment_rank_map : Fragment() {


    var rankmapDataList = arrayListOf<Rank_MapData>(
            Rank_MapData(1, "jsj", 100, 150)  ,
            Rank_MapData(2, "ㅁㅁㄴ", 100, 150),
            Rank_MapData(3, "ㄹㅇㄴ", 100, 150),
            Rank_MapData(1, "jsj", 100, 150)  ,
            Rank_MapData(2, "ㅁㅁㄴ", 100, 150),
            Rank_MapData(3, "ㄹㅇㄴ", 100, 150)
    )

   // var rankmapDataList = arrayListOf<Rank_MapData>()
//    val assetManager: AssetManager = context?.resources!!.assets
   // val inputStream= assetManager.open("datajson.json")
   // val jsonString = inputStream.bufferedReader().use { it.readText()}

  //  val jObject = JSONObject(jsonString)
 //   val jArray = jObject.getJSONArray("sampleData")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(R.layout.fragment_rank_map, container, false)

        val mAdapter = RankRecyclerViewAdapter_Map(activity!!, rankmapDataList)
        view.rank_recycler_map!!.adapter = mAdapter


        val lm = LinearLayoutManager(context)
        view.rank_recycler_map.layoutManager = lm
        view.rank_recycler_map.setHasFixedSize(true)

       // jsonRead()

        return view
    }
/*
    fun jsonRead(){
        for (i in 0 until jArray.length()) {
            val obj = jArray.getJSONObject(i)
            val name = obj.getString("sampleMapName")
            val execute = obj.getString("sampleExecute")
            val like = obj.getString("sampleLike")
        }
    }
*/

}
