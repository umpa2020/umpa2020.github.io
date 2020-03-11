package com.umpa2020.tracer.main.trace.racing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.main.ranking.RankRecyclerViewAdapterTopPlayer
import com.umpa2020.tracer.util.PrettyDistance
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*
import java.text.SimpleDateFormat
import java.util.*

class RacingFinishActivity : AppCompatActivity() {
    var activity = this
    lateinit var racerData: InfoData
    var arrRankingData: ArrayList<RankingData> = arrayListOf()
    var makerData = InfoData()


    var MapTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing_finish)
        val progressbar = ProgressBar(this)
        progressbar.show()

        // Racing Activity 에서 넘겨준 infoData를 받아서 활용
        racerData = intent.getParcelableExtra("info Data") as InfoData

        // 현재 달린 사람의 Maptitle로 메이커의 infoData를 다운 받아옴
        val db = FirebaseFirestore.getInstance()
        db.collection("mapInfo").document(racerData.mapTitle!!)
            .get()
            .addOnSuccessListener { document ->
                val dt = Date()
                val full_sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")
                val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)

                val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
                db.collection("userinfo").document(UserInfo.email).collection("user ran these maps").add(ranMapsData)

                var rankingData = RankingData(racerData.makersNickname, UserInfo.nickname, racerData.time, 1)


                db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").whereEqualTo("bestTime", 1)
                    .whereEqualTo("challengerNickname", UserInfo.nickname).get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if (racerData.time!! < document.get("challengerTime") as Long ) {
                                db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").document(document.id).update("bestTime",0)
                            }
                            else {
                                rankingData = RankingData(racerData.makersNickname, UserInfo.nickname, racerData.time, 0)
                            }
                        }

                    }

                // ranking에 내용 등록
                db.collection("rankingMap").document(racerData.mapTitle!!).set(rankingData)



                // 랭킹의 내용을 가져와서 마지막 페이지 구성
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
                                    var recycleRankingData: RankingData
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
                    makerMaxSpeedTextView.text = PrettyDistance().convertPretty(makerData.speed.max()!!)
                    makerAvgSpeedTextView.text = PrettyDistance().convertPretty(makerData.speed.average())

                    racerLapTimeTextView.text = formatter.format(Date(racerData.time!!))
                    racerMaxSpeedTextView.text = PrettyDistance().convertPretty(racerData.speed.max()!!)
                    racerAvgSpeedTextView.text = PrettyDistance().convertPretty(racerData.speed.average())

                }
                progressbar.dismiss()
            }
            .addOnFailureListener { exception ->
            }

        OKButton.setOnClickListener {
            finish()
        }

    }
}