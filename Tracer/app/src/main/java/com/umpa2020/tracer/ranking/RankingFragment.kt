package com.umpa2020.tracer.ranking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.fragment_ranking.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment() {
    lateinit var infoData: InfoData
    lateinit var infoDatas: ArrayList<InfoData>
    lateinit var rankingDownloadThread: Thread
    lateinit var strDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //TODO: return inflate~~~
        var view: View = inflater!!.inflate(R.layout.fragment_ranking, container, false)

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


        //TODO: Thread 사용하지 말고, 클래스로 빼서 getInfos 처럼 하면 배열이 받아온다는 걸 미리 알 수 있게
        //TODO: activity Created 로 이전
        rankingDownloadThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()

            db.collection("mapInfo").orderBy("execute", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    //
                    // result.forEachIndexed()
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

    fun timeSetTextView() {
        val dateFormat = SimpleDateFormat("yyyy년 MM월")
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val date = Calendar.getInstance().time
        strDate = dateFormat.format(date)
        Log.d("date", strDate)
    }

}
