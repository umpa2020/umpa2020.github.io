package com.umpa2020.tracer.main.ranking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.main.start.racing.RankingRecodeRacingActivity
import com.umpa2020.tracer.util.Chart
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.gpx.GPXConverter
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RankingMapDetailActivity : AppCompatActivity() {
  var infoData = InfoData()
  lateinit var routeGPX: RouteGPX

  var altitude: List<Double> = listOf()
  var latLngs: MutableList<MutableList<LatLng>> = mutableListOf()
  var markerlatlngs: MutableList<LatLng> = mutableListOf()
  var dbMapTitle = ""
  var profileImagePath = ""
  var uid = ""


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ranking_map_detail)
    val progressbar = ProgressBar(this)
    progressbar.show()

    val intent = intent
    //전달 받은 값으로 Title 설정
    val mapTitle = intent.extras?.getString("MapTitle").toString()
    val cutted = mapTitle.split("||")
    rankingDetailMapTitle.text = cutted[0]

    val db = FirebaseFirestore.getInstance()
    var makersNickname = ""

    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          makersNickname = document.get("makersNickname") as String
        }
        // storage 에 올린 경로를 db에 저장해두었으니 다시 역 추적 하여 프로필 이미지 반영
        db.collection("userinfo").whereEqualTo("nickname", makersNickname)
          .get()
          .addOnSuccessListener { result ->
            for (document in result) {
              profileImagePath = document.get("profileImagePath") as String
              uid = document.get("UID") as String
            }
            // glide imageview 소스
            // 프사 설정하는 코드 db -> imageView glide
            val imageView = rankingDetailProfileImage

            val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")

            val mapImageRef = storage.reference.child("Profile").child(uid).child(profileImagePath)
            mapImageRef.downloadUrl.addOnCompleteListener { task ->
              if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Glide.with(this@RankingMapDetailActivity)
                  .load(task.result)
                  .override(1024, 980)
                  .into(imageView)
                progressbar.dismiss()

              }
            }
          }
          .addOnFailureListener { exception ->
          }
      }

    db.collection("mapInfo")
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          if (document.id.equals(mapTitle)) {
            val infoData = document.toObject(InfoData::class.java)

            val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
            val routeRef = storage.reference.child("mapRoute").child(mapTitle)
            val localFile = File.createTempFile("routeGpx", "xml")
            routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
              routeGPX = GPXConverter().GpxToClass(localFile.path)
              // 1차원 배열인 고도는 그대로 받아오면 되고
              val speedList = mutableListOf<Double>()
              val elevationList = mutableListOf<Double>()
              routeGPX.trkList.forEach {
                speedList.add(it.speed.get().toDouble())
                elevationList.add(it.elevation.get().toDouble())
              }

              // 실행 수 및 db에 있는 맵타이틀을 알기위해서 (구분 시간 값 포함)
              dbMapTitle = document.id
              Logg.d("ssmm11 dbMaptitle = $dbMapTitle")

              rankingDetailDate.text = cutted[1]
              // ui 스레드 따로 빼주기
              runOnUiThread {
                rankingDetailNickname.text = infoData.makersNickname
                rankingDetailMapDetail.text = infoData.mapExplanation
                rankingDetailDistance.text = String.format("%.2f", infoData.distance!! / 1000)
                val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                rankingDetailTime.text = formatter.format(Date(infoData.time!!))
                rankingDetailSpeed.text = String.format("%.2f", speedList.average())
                val chart = Chart(elevationList, speedList, rankingDetailChart)
                chart.setChart()
              }

            }
          }
        }

        // glide imageview 소스
        val imageView = rankingDetailGoogleMap

        val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
        val mapImageRef = storage.reference.child("mapImage").child(mapTitle)

        mapImageRef.downloadUrl.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            // Glide 이용하여 이미지뷰에 로딩
            Glide.with(this@RankingMapDetailActivity)
              .load(task.result)
              .override(1024, 980)
              .into(imageView)
          }
        }


        //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        rankingDetailRaceButton.setOnClickListener {
          showPopup()
        }
      }


  }

  // 팝업 띄우는 함수
  private fun showPopup() {
    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(R.layout.ranking_map_detail_popup, null)
    val textView: TextView = view.findViewById(R.id.rankingMapDetailPopUpTextView)
    textView.text = "어떤 유형으로 경기하시겠습니까?"

    val textView1: TextView = view.findViewById(R.id.rankingMapDetailPopUpTextView1)
    //TODO. 연습용 만들면 밑의 주석 지워서 사용
    //textView1.text = "연습용 : 루트 연습용(랭킹 등록 불가능)" +
    //        "\n랭킹 기록용 : 랭킹 등록 가능"

    textView1.text = "랭킹 기록용 : 랭킹 등록 가능"

    val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
      .setTitle("유형을 선택해주세요.")
      .create()

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


    //랭킹 기록용 버튼 눌렀을 때
    val recordButton = view.findViewById<Button>(R.id.rankingMapDetailRecordButton)
    recordButton.setOnClickListener {
      val intent = Intent(App.instance.context(), RankingRecodeRacingActivity::class.java)
      intent.putExtra("RouteGPX", routeGPX)
      intent.putExtra("mapTitle", dbMapTitle)

      startActivity(intent)
    }

    alertDialog.setView(view)
    alertDialog.show() //팝업 띄우기

  }
}
