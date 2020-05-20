package com.umpa2020.tracer.main.start.challengeracing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.umpa2020.tracer.App.Companion.jobList
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.calcRank
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_challenge_racing_finish.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ChallengeRacingFinishActivity : AppCompatActivity(), OnSingleClickListener, CoroutineScope by MainScope() {
  lateinit var progressbar: ProgressBar
  lateinit var recordList: LongArray
  lateinit var bestList: LongArray
  lateinit var worstList: LongArray

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_racing_finish)
    progressbar = ProgressBar(this)
    progressbar.show()
    // Racing Activity 에서 넘겨준 infoData를 받아서 활용
    val result = intent.extras!!.getBoolean("Result")
    recordList = intent.getLongArrayExtra("RecordList")
    bestList = intent.getLongArrayExtra("BestList")
    worstList = intent.getLongArrayExtra("WorstList")
    challengeFinishMyLapTime.text=recordList.last().format(m_s)
    challengeRankTextView.text = "${recordList.last().calcRank(bestList.last(), worstList.last())} %"
    challengeOKButton.setOnClickListener(this)
    jobList.add(launch {
      // 유저 히스토리 등록
      /* FBUsersRepository().createUserHistory(
         ActivityData(racerData.mapId, Date().time, racerData.distance, racerData.time, if (result) "racing go the distance" else "racing fail")
       )*/
      challengeFinishProfileImageView.image(FBProfileRepository().getProfileImage(UserInfo.autoLoginKey))
      progressbar.dismiss()
    })
    setDistributionChart()
    setLineChart()
    challengeOKButton.setOnClickListener(this)
  }

  private fun setDistributionChart() {
    val combinedChart = distributionChart
    val rankEntryList = mutableListOf<Entry>()
    val bestRecord = bestList.last()
    val worstRecord = worstList.last()
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 0.00 + bestRecord).toFloat() / 1000, 2f))
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 0.25 + bestRecord).toFloat() / 1000, 10f))
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 0.40 + bestRecord).toFloat() / 1000, 35f))
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 0.50 + bestRecord).toFloat() / 1000, 40f))
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 0.60 + bestRecord).toFloat() / 1000, 35f))
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 0.75 + bestRecord).toFloat() / 1000, 10f))
    rankEntryList.add(Entry(((worstRecord - bestRecord) * 1.00 + bestRecord).toFloat() / 1000, 2f))
    rankEntryList.add(Entry(recordList.last().toFloat() / 1000, recordList.last().calcRank(bestRecord, worstRecord).toFloat()))
    rankEntryList.sortBy { it.x }
    Logg.d(rankEntryList.joinToString {
      "${it.x} ${it.y}\n"
    })
    val yLAxis = combinedChart.axisLeft
    yLAxis.textColor = Color.RED
    yLAxis.axisMaximum = 55f
    yLAxis.axisMinimum = 0f

    val lineDataSet = LineDataSet(rankEntryList, "Distribution")
    lineDataSet.lineWidth = 3f
    lineDataSet.color = Color.parseColor("#FFFF0000") // 파랑
    lineDataSet.setDrawHorizontalHighlightIndicator(false)
    lineDataSet.setDrawHighlightIndicators(false)
    lineDataSet.setDrawValues(false)
    lineDataSet.setDrawCircles(false)
    lineDataSet.setDrawCircleHole(false)
    lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

    val scatter = mutableListOf<Entry>()
    Logg.d("tlqkf ${recordList.joinToString {
      it.toString()
    }
    }")
    scatter.add(Entry(recordList.last().toFloat() / 1000, recordList.last().calcRank(bestRecord, worstRecord).toFloat()))
    val scatterDataSet = ScatterDataSet(scatter, "MyRecord").apply {
      setColors(Color.parseColor("#FF0000FF"))
      scatterShapeSize = 20f
      setDrawValues(true)
      valueTextSize = 10f
    }

    // 속도 셋팅
    val combinedData = CombinedData()
    combinedData.setData(LineData(lineDataSet))
    combinedData.setData(ScatterData(scatterDataSet))
    combinedChart.isDoubleTapToZoomEnabled = false // 더블 클릭 막기
    combinedChart.setDrawGridBackground(false)
    combinedChart.animateY(2000, Easing.EasingOption.EaseInCubic)
    combinedChart.data = combinedData
    combinedChart.invalidate()
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      // 다 봤다는 표시 - 그래도 앞에 있던 액티비티들을 끄고, 메인 엑티비티 실행
      R.id.challengeOKButton -> {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
      }
    }
  }

  fun setLineChart() {    //클래스로 따로 빼야할듯
    //TODO : best worst 라인 추가
    val lineChart = rankLineChart
    val recordEntryList = mutableListOf<Entry>()
    recordList.forEachIndexed { i, d ->
      if (i == recordList.size - 1) return@forEachIndexed
      recordEntryList.add(Entry(i.toFloat(), (recordList[i + 1] - d).toFloat() / 1000))
    }
    Logg.d("Size : ${recordEntryList.size}")
    val bestEntryList = mutableListOf<Entry>()
    bestList.forEachIndexed { i, d ->
      if (i == bestList.size - 1) return@forEachIndexed
      bestEntryList.add(Entry(i.toFloat(), (bestList[i + 1] - d).toFloat() / 1000))
    }
    val worstEntryList = mutableListOf<Entry>()
    worstList.forEachIndexed { i, d ->
      if (i == worstList.size - 1) return@forEachIndexed
      worstEntryList.add(Entry(i.toFloat(), (worstList[i + 1] - d).toFloat() / 1000))
    }
    val xAxis = lineChart.xAxis
    xAxis.isEnabled = false
    lineChart.axisLeft.run {
      textColor = Color.RED
      axisMaximum = 100f
      axisMinimum = 0f
      isInverted = true
    }
    lineChart.axisRight.setDrawLabels(false)

    val lineData = LineData(getLineDataSet(recordEntryList, "My record", "#FFFF0000"))
    lineData.addDataSet(getLineDataSet(bestEntryList, "Best Record", "#FF00FF00"))
    lineData.addDataSet(getLineDataSet(worstEntryList, "Worst Record", "#FF0000FF"))

    lineChart.isDoubleTapToZoomEnabled = false // 더블 클릭 막기
    lineChart.setDrawGridBackground(false)
    lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)
    lineChart.data = lineData
    lineChart.invalidate()

  }

  fun getLineDataSet(entryList: MutableList<Entry>, label: String, colorString: String): LineDataSet {
    // 속도 셋팅
    return LineDataSet(entryList, label).apply {
      lineWidth = 3f
      color = Color.parseColor(colorString) // 파랑
      setDrawHorizontalHighlightIndicator(false)
      setDrawHighlightIndicators(false)
      setDrawValues(false)
      setDrawCircles(false)
      setDrawCircleHole(false)
      axisDependency = YAxis.AxisDependency.LEFT
      mode = LineDataSet.Mode.CUBIC_BEZIER
    }
  }
}
