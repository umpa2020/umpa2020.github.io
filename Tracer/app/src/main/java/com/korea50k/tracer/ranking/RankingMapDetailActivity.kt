package com.korea50k.tracer.ranking

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import java.util.ArrayList

class RankingMapDetailActivity : AppCompatActivity() {
    var infoData = InfoData()
    var altitude: List<Double> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking_map_detail)

        val intent = getIntent()
        //전달 받은 값으로 Title 설정
        var mapTitle = intent.extras?.getString("MapTitle").toString()
        rankingDetailMapTitle.text = mapTitle

        val db = FirebaseFirestore.getInstance()

        db.collection("mapRoute")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var cutting = document.id.split("||")
                    if (cutting[0].equals(mapTitle)) {
                        altitude = document.get("altitude") as List<Double>
                    }
                }

                db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            infoData = document.toObject(InfoData::class.java)
                            rankingDetailDate.text = document.id
                        }
                        rankingDetailNickname.text = infoData.makersNickname
                        rankingDetailMapDetail.text = infoData.mapExplanation
                        rankingDetailDistance.text = infoData.distance.toString()
                        rankingDetailTime.text = infoData.time.toString()
                        rankingDetailSpeed.text = infoData.speed.average().toString()
                        setChart(infoData.speed)

                        //adpater 추가
                    }
                    .addOnFailureListener { exception ->
                    }
            }
            .addOnFailureListener { exception ->
            }

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

        //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        rankingDetailRaceButton.setOnClickListener {
            showPopup()
        }
    }

    /**
     * 팝업 띄우는 함수
     * */
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
            Toast.makeText(this, "PracticeButton 클릭", Toast.LENGTH_SHORT).show()
        }


        //랭킹 기록용 버튼 눌렀을 때
        val recordButton = view.findViewById<Button>(R.id.rankingMapDetailRecordButton)
        recordButton.setOnClickListener {
            Toast.makeText(this, "RecordButton 클릭", Toast.LENGTH_SHORT).show()
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
