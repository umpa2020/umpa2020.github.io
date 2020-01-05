package com.korea50k.RunShare.Activities.RankFragment


import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.ConvertJson
import com.korea50k.RunShare.dataClass.RankMapData
import kotlinx.android.synthetic.main.fragment_rank_map.view.*

class fragment_rank_map : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(R.layout.fragment_rank_map, container, false)


        val assetManager = resources.assets

        //TODO:서버에서 데이터 가져와서 해야함
        val inputStream= assetManager.open("datajson")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        var rankMapDatas = ConvertJson.JsonToRankMapDatas(jsonString)

        //리사이클러 뷰 클릭 리스너 부분
        val mAdapter = RankRecyclerViewAdapter_Map(activity!!, rankMapDatas){ rankmapdata ->
            //TODO Intent로 새로운 xml 열기
            val intent = Intent(context, RankRecyclerClickActivity::class.java)
            startActivity(intent)
        }
        view.rank_recycler_map!!.adapter = mAdapter


        val lm = LinearLayoutManager(context)
        view.rank_recycler_map.layoutManager = lm
        view.rank_recycler_map.setHasFixedSize(true)


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
