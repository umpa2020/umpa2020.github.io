package com.korea50k.tracer.start

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R
import com.korea50k.tracer.UserInfo
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.Privacy
import com.korea50k.tracer.dataClass.RankingData
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.map.ViewerMap
import kotlinx.android.synthetic.main.activity_running_save.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RunningSaveActivity : AppCompatActivity() {
    var switch = 0

    lateinit var infoData: InfoData
    lateinit var routeData: RouteData

    lateinit var map: ViewerMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_save)

        infoData = intent.getParcelableExtra("Info Data")
        routeData = intent.getParcelableExtra("Route Data")

        //TODO: 액티비티에 그리는 거 먼저

        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = ViewerMap(smf, this, routeData)
        distance_tv.text = String.format("%.3f", infoData.distance!! / 1000)
        val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

        time_tv.text = formatter.format(Date(infoData.time!!))

        speed_tv.text = String.format("%.3f", infoData.speed.average())
        if (infoData.privacy == Privacy.PUBLIC) {
            racingRadio.isChecked = false
            racingRadio.isEnabled = false
            publicRadio.isChecked = true
        }
        setChart()

        //TODO:db에 있는 모든 mapdata 받아오는 클래스
/*

        var getMapData = GetMapData()
        getMapData.getMapData()

        // Reference to an image file in Cloud Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val mapImageRef = storageReference.child("images/dog.jpg")

        Log.d("ssmm11", "storage = " + storageReference)
*/




    }

    fun onClick(view: View) {
        if (switch == 0) {
            when (view.id) {
                R.id.save_btn -> {
                    if (mapTitleEdit.text.toString() == "") {
                        mapTitleEdit.hint = "제목을 설정해주세요"
                        mapTitleEdit.setHintTextColor(Color.RED)
                    } else if (mapExplanationEdit.text.toString() == "") {
                        mapExplanationEdit.hint = "맵 설명을 작성해주세요"
                        mapExplanationEdit.setHintTextColor(Color.RED)
                    } else {
                        map.CaptureMapScreen()
                        switch++
                    }
                }
            }
        }
    }

    fun save(imgPath: String) {
        infoData.mapImage = imgPath
        infoData.mapTitle = mapTitleEdit.text.toString()
        infoData.mapExplanation = mapExplanationEdit.text.toString()
        infoData.makersNickname = UserInfo.email
        infoData.execute = 0
        infoData.likes = 0

        when (privacyRadioGroup.checkedRadioButtonId) {
            R.id.racingRadio -> infoData.privacy = Privacy.RACING
            R.id.publicRadio -> infoData.privacy = Privacy.PUBLIC
            R.id.privateRadio -> infoData.privacy = Privacy.PRIVATE
        }

        // 맵 타이틀과, 랭킹 중복 방지를 위해서 시간 값을 넣어서 중복 방지
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")

        val db = FirebaseFirestore.getInstance()

        // InfoData class upload to database 참조 - 루트를 제외한 맵 정보 기술
        db.collection("mapInfo").document(infoData.mapTitle+"||" + full_sdf.format(dt).toString()).set(infoData)

        // RouteData class upload to database 참조 - 루트 정보만 표기 (위도경도, 고도, 마커의 위도경도)
        db.collection("mapRoute").document(infoData.mapTitle+"||" + full_sdf.format(dt).toString()).set(routeData)

        // RankingData class upload to database - db 구조 정립 필요
        val recordData = RankingData("jungbin", "13:33")
        db.collection("maps").document(infoData.mapTitle+"||" + full_sdf.format(dt).toString()).collection("record")
            .document("jungbin||" + full_sdf.format(dt).toString()).set(recordData)
        // db에 원하는 경로 및, 문서로 업로드


        // storage에 이미지 업로드
        val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
        val mapImageRef = storage.reference.child("mapImage").child(infoData.mapTitle!!)
        var uploadTask = mapImageRef.putFile(Uri.fromFile(File(imgPath)))
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener {
            finish()
        }


        //TODO:ImageView 에 이미지 박는 코드 (firebase)

        val imageView = saveTestImageview

        mapImageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Log.d("ssmm11", "흐음"+mapImageRef.downloadUrl)
                Glide.with(this@RunningSaveActivity)
                    .load(task.result)
                    .override(1024, 980)
                    .into(imageView)
            } else {
                Log.d("ssmm11", "실패")
            }
        }
    }

    private fun setChart() {    //클래스로 따로 빼야할듯
        var lineChart = chart as CombinedChart
        val alts = ArrayList<BarEntry>()
        val speeds = ArrayList<Entry>()

        for (index in routeData.altitude?.indices!!) {
            alts.add(BarEntry(index.toFloat(), routeData.altitude!![index]?.toFloat()))
            speeds.add(Entry(index.toFloat(), infoData.speed[index].toFloat()))
        }

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.axisLeft
        yLAxis.textColor = Color.RED
        yLAxis.axisMaximum = infoData.speed.max()!!.toFloat() + 5
        yLAxis.axisMinimum = 0F

        val yRAxis = lineChart.axisRight
        yRAxis.textColor = Color.BLUE
        yRAxis.axisMaximum = routeData.altitude?.max()!!.toFloat() + 5
        yRAxis.axisMinimum = routeData.altitude?.min()!!.toFloat() - 5


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
