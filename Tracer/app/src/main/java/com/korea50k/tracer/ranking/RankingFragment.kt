package com.korea50k.tracer.ranking


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class RankingFragment : Fragment() {
    lateinit var infoData: InfoData
    lateinit var infoDatas: ArrayList<InfoData>
    lateinit var rankingDownloadThread: Thread
    lateinit var strDate : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View = inflater!!.inflate(R.layout.fragment_ranking, container, false)

        val progressbar = ProgressBar(context!!)
        progressbar.show()
        /**
        * TextView에 현재 날짜, 월 입력하는 함수
         */
        timeSetTextView()
        view!!.rankingFragmentMonthTextView.text = strDate

        infoDatas = arrayListOf()

        //레이아웃 매니저 추가
        view.rank_recycler_map.layoutManager = LinearLayoutManager(context)

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
                    progressbar.dismiss()
                }
                .addOnFailureListener { exception ->
                }
        })

        rankingDownloadThread.start()
        return view
    }

    fun timeSetTextView(){

        var dateFormat = SimpleDateFormat("yyyy년 MM월")
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        var date = Calendar.getInstance().time
        strDate = dateFormat.format(date)
        Log.d("date", strDate)
    }

}
