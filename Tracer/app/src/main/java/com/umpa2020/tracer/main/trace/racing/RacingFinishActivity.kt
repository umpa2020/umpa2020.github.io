package com.umpa2020.tracer.main.trace.racing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.main.MainActivity
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
        val result = intent.extras!!.getBoolean("Result")
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)

        //TODO:NETWORK DB작업 클래스화 (안정화 되면 진행 예정..)
        if (result) { // 성공인 경우
            // 현재 달린 사람의 Maptitle로 메이커의 infoData를 다운 받아옴
            val db = FirebaseFirestore.getInstance()
            db.collection("mapInfo").document(racerData.mapTitle!!)
                .get()
                .addOnSuccessListener { document ->

                    val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
                    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user ran these maps").add(ranMapsData)

                    var rankingData = RankingData(racerData.makersNickname, UserInfo.nickname, racerData.time, 1)

                    // 랭킹 맵에서
                    db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").whereEqualTo("bestTime", 1)
                        .whereEqualTo("challengerNickname", UserInfo.nickname).get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d("ssmm11", "racer time = " + racerData.time)
                                Log.d("ssmm11", "\ndocument.get(\"challengerTime\") as Long = " + document.get("challengerTime") as Long)
                                if (racerData.time!!.toLong() < document.get("challengerTime") as Long) {
                                    Log.d("ssm11", "원래가 더 느리다 ")

                                    db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").document(document.id).update("bestTime", 0)

                                } else {
                                    Log.d("ssmm11", "안드 위 베타 = " + rankingData.bestTime)
                                    rankingData.bestTime = 0
                                    Log.d("ssmm11", "안드 아래 베타 = " + rankingData.bestTime)
                                }
                                break
                            }

                            // 타임스탬프
                            val dt = Date()
                            val full_sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())

                            val date = full_sdf.parse(dt.toString())
                            val timestamp = date!!.time

                            // 랭킹의 내용을 가져와서 마지막 페이지 구성
                            db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking")
                                .document(UserInfo.autoLoginKey + timestamp).set(rankingData)
                                .addOnSuccessListener {
                                    db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").orderBy("challengerTime", Query.Direction.ASCENDING)
                                        .get()
                                        .addOnSuccessListener { result ->
                                            var index = 1
                                            for (document in result) {
                                                if (document.get("challengerNickname") == UserInfo.nickname && document.get("challengerTime").toString().equals(racerData.time.toString())) {
                                                    resultRankTextView.text = "" + index + "등"
                                                }
                                                var recycleRankingData: RankingData
                                                recycleRankingData = document.toObject(RankingData::class.java)
                                                if (recycleRankingData.bestTime == 1) {
                                                    //최대 10위까지만 띄우기
                                                    if (arrRankingData.size > 10)
                                                        break

                                                    arrRankingData.add(recycleRankingData)

                                                }
                                                index++
                                            }
                                            //레이아웃 매니저 추가
                                            resultPlayerRankingRecycler.layoutManager = LinearLayoutManager(this)
                                            //adpater 추가
                                            resultPlayerRankingRecycler.adapter = RankRecyclerViewAdapterTopPlayer(arrRankingData)

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
                                }
                        }
                }

                .addOnFailureListener { exception ->
                }
        } else {
            resultRankTextView.text = "실패"
            makerLapTimeTextView.text = formatter.format(Date(makerData.time!!))
            makerMaxSpeedTextView.text = PrettyDistance().convertPretty(makerData.speed.max()!!)
            makerAvgSpeedTextView.text = PrettyDistance().convertPretty(makerData.speed.average())

            racerLapTimeTextView.text = formatter.format(Date(racerData.time!!))
            racerMaxSpeedTextView.text = PrettyDistance().convertPretty(racerData.speed.max()!!)
            racerAvgSpeedTextView.text = PrettyDistance().convertPretty(racerData.speed.average())

            progressbar.dismiss()


        }

        OKButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}