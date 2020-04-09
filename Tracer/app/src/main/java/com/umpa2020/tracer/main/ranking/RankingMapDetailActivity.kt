package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.MM_SS
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.network.FBMapImage
import com.umpa2020.tracer.network.FBProfile
import com.umpa2020.tracer.util.Chart
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import java.io.File

class RankingMapDetailActivity : AppCompatActivity() {
  lateinit var routeGPX: RouteGPX
  var dbMapTitle = ""


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ranking_map_detail)

    val intent = intent
    //전달 받은 값으로 Title 설정
    val mapTitle = intent.extras?.getString("MapTitle").toString()
    val cutted = mapTitle.split("||")
    rankingDetailMapTitle.text = cutted[0]
    //TODO : 날짜로 바꿔야함 (한국 시간만 해결하면 됨)
    // TODO : 메인에서 마커 클릭하면 여기서 어플 터짐
    Logg.d(cutted[1])


    rankingDetailDate.text = cutted[1].toLong().format("yyyy-MM-dd HH:mm:ss")


    val db = FirebaseFirestore.getInstance()
    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          FBProfile().getProfileImage(rankingDetailProfileImage, document.get("makersNickname") as String)
          break
        }
      }

    db.collection("mapInfo")
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          if (document.id.equals(mapTitle)) {
            val infoData = document.toObject(InfoData::class.java)

            val storage = FirebaseStorage.getInstance()
            val routeRef = storage.reference.child("mapRoute").child(mapTitle)
            val localFile = File.createTempFile("routeGpx", "xml")
            routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
              routeGPX = localFile.path.gpxToClass()
              // 1차원 배열인 고도는 그대로 받아오면 되고
              val speedList = mutableListOf<Double>()
              val elevationList = mutableListOf<Double>()
              routeGPX.trkList.forEach {
                speedList.add(it.speed.get().toDouble())
                elevationList.add(it.elevation.get().toDouble())
              }

              // 실행 수 및 db에 있는 맵타이틀을 알기위해서 (구분 시간 값 포함)
              dbMapTitle = document.id


              // ui 스레드 따로 빼주기
              runOnUiThread {
                rankingDetailNickname.text = infoData.makersNickname
                rankingDetailMapDetail.text = infoData.mapExplanation
                rankingDetailDistance.text = String.format("%.2f", infoData.distance!! / 1000)
                rankingDetailTime.text = infoData.time!!.format(MM_SS)
                rankingDetailSpeed.text = String.format("%.2f", speedList.average())
                val chart = Chart(elevationList, speedList, rankingDetailChart)
                chart.setChart()
              }

            }
          }
        }
        FBMapImage().getMapImage(rankingDetailGoogleMap, mapTitle)

        //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        rankingDetailRaceButton.setOnClickListener {
          showPopup()
        }
      }


  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopup // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  private fun showPopup() {


    //TODO. 연습용 만들면 밑의 주석 지워서 사용
    /*
    //연습용 버튼 눌렀을 때
    val practiceButton = view.findViewById<Button>(R.id.rankingMapDetailPracticeButton)
    practiceButton.setOnClickListener {
        val nextIntent = Intent(this, PracticeRacingActivity::class.java)
        nextIntent.putExtra("makerRouteData", routeData)
        nextIntent.putExtra("maptitle", dbMapTitle)
        startActivity(nextIntent)
    }

     */
    noticePopup = ChoicePopup(this, "유형을 선택해주세요.",
      "어떤 유형으로 경기하시겠습니까? \n\n랭킹 기록용 : 랭킹 등록 가능",
      "기록용", "",
      View.OnClickListener {
        //랭킹 기록용 버튼 눌렀을 때
        val intent = Intent(App.instance.context(), RacingActivity::class.java)
        intent.putExtra("RouteGPX", routeGPX)
        intent.putExtra("mapTitle", dbMapTitle)
        noticePopup.dismiss()
        startActivity(intent)
        finish()
      },
      View.OnClickListener {
        noticePopup.dismiss()
      })
    noticePopup.show()

  }
}
