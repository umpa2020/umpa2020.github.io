package com.korea50k.tracer.ranking


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import kotlinx.android.synthetic.main.fragment_ranking.view.*

/**
 * A simple [Fragment] subclass.
 */
class RankingFragment : Fragment() {
    lateinit var infoData: InfoData
    lateinit var infoDatas: ArrayList<InfoData>
    lateinit var rankingDownloadThread: Thread

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View = inflater!!.inflate(R.layout.fragment_ranking, container, false)

        infoDatas = arrayListOf()

        //레이아웃 매니저 추가
        view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

        //TODO:레이싱 추가되면 실행 순으로 쿼리 날리는 거 넣어야 함
        rankingDownloadThread = Thread( Runnable {
            val db = FirebaseFirestore.getInstance()

            db.collection("mapInfo").orderBy("execute", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        infoData = document.toObject(InfoData::class.java)
                        infoData.mapTitle = document.id
                        infoDatas.add(infoData)
                    }
                    //adpater 추가
                    view.rank_recycler_map.adapter = RankRecyclerViewAdapterMap(infoDatas)

                }
                .addOnFailureListener { exception ->
                }
        })

        rankingDownloadThread.start()
        return view
    }

}
