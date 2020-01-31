package com.korea50k.tracer.ranking


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import kotlinx.android.synthetic.main.fragment_ranking.view.*

/**
 * A simple [Fragment] subclass.
 */
class RankingFragment : Fragment() {
    lateinit var infoData: InfoData
    lateinit var infoDatas: ArrayList<InfoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View = inflater!!.inflate(R.layout.fragment_ranking, container, false)

        infoDatas = arrayListOf()

        //레이아웃 매니저 추가
        view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

        val db = FirebaseFirestore.getInstance()

        db.collection("mapInfo")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    infoData = document.toObject(InfoData::class.java)
                    infoDatas.add(infoData)
                }
                //adpater 추가
                view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas)

            }
            .addOnFailureListener { exception ->
            }



        return view
    }

}
