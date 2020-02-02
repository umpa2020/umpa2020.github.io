package com.korea50k.tracer.ranking

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.racing.PracticeRacingActivity
import com.korea50k.tracer.racing.RankingRecodeRacingActivity
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import java.util.*

class RankingMapDetailActivity : AppCompatActivity() {
    var infoData = InfoData()
    var routeData = RouteData()

    var altitude: List<Double> = listOf()
    var latLngs: MutableList<MutableList<LatLng>> = mutableListOf()
    var markerlatlngs: MutableList<LatLng> = mutableListOf()
    var dbMapTitle =""

    lateinit var rankingDetailThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking_map_detail)

        val intent = getIntent()
        //전달 받은 값으로 Title 설정
        var mapTitle = intent.extras?.getString("MapTitle").toString()
        rankingDetailMapTitle.text = mapTitle

        rankingDetailThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()

            db.collection("mapRoute")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        var cutting = document.id.split("||")
                        if (cutting[0].equals(mapTitle)) {
                            // 1차원 배열인 고도는 그대로 받아오면 되고
                            altitude = document.get("altitude") as List<Double>

                            // 마커의 LatLng 는 나눠서 넣어줘야함
                            var receiveMarkerDatas = document.get("markerlatlngs") as List<Object>

                            for (receiveMarkerData in receiveMarkerDatas) {
                                val location = receiveMarkerData as Map<String, Any>
                                val latLng = LatLng(
                                    location["latitude"] as Double,
                                    location["longitude"] as Double
                                )
                                markerlatlngs.add(latLng)
                            }

                            // 실행 수 및 db에 있는 맵타이틀을 알기위해서 (구분 시간 값 포함)
                            dbMapTitle = document.id

                            // 2차원 배열은 새로 나누어 담아서 받음 1차원 배열 만들고 2차원 배열에 add
                            // 맵 위도경도 받아오기
                            db.collection("mapRoute").document(document.id).collection("routes")
                                .get()
                                .addOnSuccessListener { result2 ->
                                    for (document2 in result2) {
                                        var routeArray: MutableList<LatLng> = mutableListOf()
                                        var receiveRouteDatas = document2.get("latlngs") as MutableList<Object>
                                        for (receiveRouteData in receiveRouteDatas) {
                                            val location = receiveRouteData as Map<String, Any>
                                            val latLng = LatLng(
                                                location["latitude"] as Double,
                                                location["longitude"] as Double
                                            )
                                            routeArray.add(latLng)
                                        }
                                        latLngs.add(routeArray)
                                    }
                                    routeData = RouteData(altitude, latLngs, markerlatlngs)
                                }
                        }
                    }

                    // 단순 맵 정보 받아오는 부분
                    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                infoData = document.toObject(InfoData::class.java)
                                rankingDetailDate.text = document.id
                            }
                            // ui 스레드 따로 빼주기
                            runOnUiThread {
                                rankingDetailNickname.text = infoData.makersNickname
                                rankingDetailMapDetail.text = infoData.mapExplanation
                                rankingDetailDistance.text = infoData.distance.toString()
                                rankingDetailTime.text = infoData.time.toString()
                                rankingDetailSpeed.text = infoData.speed.average().toString()
                                setChart(infoData.speed)
                            }
                        }
                        .addOnFailureListener { exception ->
                        }
                }
                .addOnFailureListener { exception ->
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
                } else {
                    Log.d("ssmm11", "실패")
                }
            }
        })

        rankingDetailThread.start()

        //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        rankingDetailRaceButton.setOnClickListener {
            showPopup()
        }
    }


    // 팝업 띄우는 함수
    private fun showPopup() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.ranking_map_detail_popup, null)
        val textView: TextView = view.findViewById(R.id.rankingMapDetailPopUpTextView)
        textView.text = "어떤 유형으로 경기하시겠습니까?"

        val textView1: TextView = view.findViewById(R.id.rankingMapDetailPopUpTextView1)
        textView1.text = "연습용 : 루트 연습용(랭킹 등록 불가능)" +
                "\n랭킹 기록용 : 랭킹 등록 가능"

        val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
            .setTitle("유형을 선택해주세요.")
            .create()

        //연습용 버튼 눌렀을 때
        val practiceButton = view.findViewById<Button>(R.id.rankingMapDetailPracticeButton)
        practiceButton.setOnClickListener {
            val nextIntent = Intent(this, PracticeRacingActivity::class.java)
            nextIntent.putExtra("makerRouteData", routeData)
            nextIntent.putExtra("maptitle", dbMapTitle)
            startActivity(nextIntent)
        }


        //랭킹 기록용 버튼 눌렀을 때
        val recordButton = view.findViewById<Button>(R.id.rankingMapDetailRecordButton)
        recordButton.setOnClickListener {
            val nextIntent = Intent(this, RankingRecodeRacingActivity::class.java)
            nextIntent.putExtra("makerRouteData", routeData)
            nextIntent.putExtra("maptitle", dbMapTitle)
            startActivity(nextIntent)
        }

        alertDialog.setView(view)
        alertDialog.show() //팝업 띄우기

    }

    private fun setChart(speed: MutableList<Double> = mutableListOf(.0)) {    //클래스로 따로 빼야할듯
        var lineChart = rankingDetailChart as CombinedChart
        val alts = ArrayList<BarEntry>()
        val speeds = ArrayList<Entry>()

        for (index in altitude.indices) {
            alts.add(BarEntry(index.toFloat(), altitude[index].toFloat()))
            speeds.add(Entry(index.toFloat(), speed[index].toFloat()))
        }

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.axisLeft
        yLAxis.textColor = Color.RED
        yLAxis.axisMaximum = speed.max()!!.toFloat() + 5
        yLAxis.axisMinimum = 0F

        val yRAxis = lineChart.axisRight
        yRAxis.textColor = Color.BLUE
        yRAxis.axisMaximum = altitude.max()!!.toFloat() + 5
        yRAxis.axisMinimum = altitude.min()!!.toFloat() - 5


        // 고도 셋팅
        var altsData = LineData()

//        val lineDataSet = LineDataSet(alts, "고도")
//        lineDataSet.lineWidth = 1.5f
//        lineDataSet.color = Color.parseColor("#FF0000FF") // 파랑
//        lineDataSet.setDrawHorizontalHighlightIndicator(false)
//        lineDataSet.setDrawHighlightIndicators(false)
//        lineDataSet.setDrawValues(false)
//       // lineDataSet.setCircleColor(Color.parseColor("#FFFFFFFF"))
//        lineDataSet.setDrawCircles(false)
//        lineDataSet.setDrawCircleHole(false)
//        lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        val set = BarDataSet(alts, "고도")
        set.color = Color.parseColor("#FF0000FF") // 파랑
        set.barBorderColor = Color.parseColor("#FF0000FF") // 파랑
        set.barBorderWidth = 3f
        set.setDrawValues(false)
        set.setDrawIcons(false)
        set.isHighlightEnabled = false
        set.axisDependency = YAxis.AxisDependency.RIGHT

        //altsData.addDataSet(set)

        // 스피드 셋팅
        var speedsData = LineData()
        val lineDataSet2 = LineDataSet(speeds, "속력")
        lineDataSet2.lineWidth = 1.5f
        lineDataSet2.color = Color.parseColor("#FFFF0000") // 빨강
        lineDataSet2.setDrawHorizontalHighlightIndicator(false)
        lineDataSet2.setDrawHighlightIndicators(false)
        lineDataSet2.setDrawValues(false)
//        lineDataSet2.setCircleColor(Color.parseColor("#FFFF0000"))
//        lineDataSet2.setCircleColorHole(Color.parseColor("#FFFF0000"))
        lineDataSet2.setDrawCircles(false)
        lineDataSet2.setDrawCircleHole(false)
        lineDataSet2.axisDependency = YAxis.AxisDependency.LEFT

        speedsData.addDataSet(lineDataSet2)

        var combinedData = CombinedData()

        combinedData.setData(BarData(set))
        combinedData.setData(speedsData)

//        val lineData = LineData(lineDataSet)
//        lineChart.data = lineData
//        lineChart.data.addDataSet(lineDataSet2)


        //val description = Description()
        //description.text = ""

        lineChart.isDoubleTapToZoomEnabled = false // 더블 클릭 막기
        lineChart.setDrawGridBackground(false)
        //lineChart.description = description
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)

        lineChart.data = combinedData
        lineChart.invalidate()
    }
}
