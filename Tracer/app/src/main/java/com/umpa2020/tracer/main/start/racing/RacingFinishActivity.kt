package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.main.ranking.RankRecyclerViewAdapterTopPlayer
import com.umpa2020.tracer.util.Logg
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
    racerData = intent.getParcelableExtra("InfoData") as InfoData
    val result = intent.extras!!.getBoolean("Result")
    val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
    val routeGPX = intent.getParcelableExtra<RouteGPX>("RouteGPX")
    val mapRouteGPX = intent.getParcelableExtra<RouteGPX>("MapRouteGPX")
    val racerSpeeds = routeGPX!!.getSpeed()
    val makerSpeeds = mapRouteGPX!!.getSpeed()

    if (result) { // 성공인 경우
      // 현재 달린 사람의 Maptitle로 메이커의 infoData를 다운 받아옴
      val db = FirebaseFirestore.getInstance()
      db.collection("mapInfo").document(racerData.mapTitle!!)
        .get()
        .addOnSuccessListener { document ->
          makerData = document.toObject(InfoData::class.java)!!
          val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
          db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user ran these maps").add(ranMapsData)

          val rankingData = RankingData(racerData.makersNickname, UserInfo.nickname, racerData.time, 1)

          // 랭킹 맵에서
          db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").whereEqualTo("bestTime", 1)
            .whereEqualTo("challengerNickname", UserInfo.nickname).get()
            .addOnSuccessListener { result ->
              for (document in result) {
                if (racerData.time!!.toLong() < document.get("challengerTime") as Long) {
                  db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").document(document.id).update("bestTime", 0)

                } else {
                  rankingData.bestTime = 0
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
                      for (document2 in result) {
                        if (document2.get("challengerNickname") == UserInfo.nickname && document2.get("challengerTime").toString().equals(racerData.time.toString())) {
                          resultRankTextView.text = "" + index + "등"
                        }
                        var recycleRankingData: RankingData
                        recycleRankingData = document2.toObject(RankingData::class.java)
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

                      runOnUiThread {
                        Logg.d("makerData.time = ${makerData.time}")
                        Logg.d("makerData.time = ${racerData.time}")

                        makerLapTimeTextView.text = formatter.format(Date(makerData.time!!))
                        makerMaxSpeedTextView.text = PrettyDistance().convertPretty(makerSpeeds.max()!!)
                        makerAvgSpeedTextView.text = PrettyDistance().convertPretty(makerSpeeds.average())

                        racerLapTimeTextView.text = formatter.format(Date(racerData.time!!))
                        racerMaxSpeedTextView.text = PrettyDistance().convertPretty(racerSpeeds.max()!!)
                        racerAvgSpeedTextView.text = PrettyDistance().convertPretty(racerSpeeds.average())
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
      makerMaxSpeedTextView.text = PrettyDistance().convertPretty(makerSpeeds.max()!!)
      makerAvgSpeedTextView.text = PrettyDistance().convertPretty(makerSpeeds.average())

      racerLapTimeTextView.text = formatter.format(Date(racerData.time!!))
      racerMaxSpeedTextView.text = PrettyDistance().convertPretty(racerSpeeds.max()!!)
      racerAvgSpeedTextView.text = PrettyDistance().convertPretty(racerSpeeds.average())

      progressbar.dismiss()
    }

    OKButton.setOnClickListener {
      val intent = Intent(this, MainActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
      startActivity(intent)
    }
  }

  private fun RouteGPX.getSpeed(): MutableList<Double> {
    val speeds = mutableListOf<Double>()
    trkList.forEach {
      speeds.add(it.speed.get().toDouble())
    }
    return speeds
  }
}