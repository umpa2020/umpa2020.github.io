package com.umpa2020.tracer.racing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.ranking.RankRecyclerViewAdapterTopPlayer
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RacingFinishActivity : AppCompatActivity() {
    var activity = this
    lateinit var racerData: InfoData
    var arrRankingData: ArrayList<RankingData> = arrayListOf()
    var makerData = InfoData()

    lateinit var makerInfoDataDownload: Thread

    var MapTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing_finish)
        val progressbar = ProgressBar(this)
        progressbar.show()

        // Racing Activity 에서 넘겨준 infoData를 받아서 활용
        racerData = intent.getParcelableExtra("info Data") as InfoData

        // 현재 달린 사람의 Maptitle로 메이커의 infoData를 다운 받아옴
        makerInfoDataDownload = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()
            db.collection("mapInfo").document(racerData.mapTitle!!)
                .get()
                .addOnSuccessListener { document ->
                    val dt = Date()
                    val full_sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")
                    val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)

                    val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
                    db.collection("userinfo").document(UserInfo.email).collection("user ran these maps").add(ranMapsData)

                    val rankingData = RankingData(racerData.makersNickname, UserInfo.nickname, racerData.time)

                    // ranking에 내용 등록
                    db.collection("rankingMap").document(racerData.mapTitle!!).set(rankingData)
                    db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking")
                        .document(UserInfo.nickname + "||" + full_sdf.format(dt)).set(rankingData)
                        .addOnSuccessListener {
                            db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").orderBy("challengerTime", Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener { result ->
                                    var index = 1
                                    for (document in result) {
                                        if (document.id == rankingData.challengerNickname + "||" + full_sdf.format(dt)) {
                                            resultRankTextView.text = "" + index + "등"
                                        }
                                        var recycleRankingData = RankingData()
                                        recycleRankingData = document.toObject(RankingData::class.java)
                                        arrRankingData.add(recycleRankingData)
                                        index++
                                    }
                                    //레이아웃 매니저 추가
                                    resultPlayerRankingRecycler.layoutManager = LinearLayoutManager(this)
                                    //adpater 추가
                                    resultPlayerRankingRecycler.adapter = RankRecyclerViewAdapterTopPlayer(arrRankingData)
                                    progressbar.dismiss()
                                }
                                .addOnFailureListener { exception ->
                                }
                        }
                    makerData = document.toObject(InfoData::class.java)!!
                    runOnUiThread {
                        makerLapTimeTextView.text = formatter.format(Date(makerData.time!!))
                        makerMaxSpeedTextView.text = String.format("%.2f", makerData.speed.max())
                        makerAvgSpeedTextView.text = String.format("%.2f", makerData.speed.average())

                        racerLapTimeTextView.text = formatter.format(Date(racerData.time!!))
                        racerMaxSpeedTextView.text = String.format("%.2f", racerData.speed.max())
                        racerAvgSpeedTextView.text = String.format("%.2f", racerData.speed.average())


                    }
                    progressbar.dismiss()
                }
                .addOnFailureListener { exception ->
                }
        })
        makerInfoDataDownload.start()

        OKButton.setOnClickListener {
            finish()
        }

    }
}