package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.MM_SS
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.extensions.toRank
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.main.ranking.RankRecyclerViewAdapterTopPlayer
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.activity_racing_finish.*
import java.util.*


/* 핸들러로 받아서 사용할 것
//레이아웃 매니저 추가
 */

class RacingFinishActivity : AppCompatActivity(), OnSingleClickListener {
  val GETMAKERDATA = 100
  val GETRACING = 101

  var activity = this
  lateinit var racerData: InfoData
  lateinit var makerData: InfoData
  var arrRankingData: ArrayList<RankingData> = arrayListOf()
  lateinit var progressbar: ProgressBar
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_finish)

    progressbar = ProgressBar(this)
    progressbar.show()


    // Racing Activity 에서 넘겨준 infoData를 받아서 활용
    racerData = intent.getParcelableExtra("InfoData") as InfoData
    val result = intent.extras!!.getBoolean("Result")
    val routeGPX = intent.getParcelableExtra<RouteGPX>("RouteGPX")
    val mapRouteGPX = intent.getParcelableExtra<RouteGPX>("MapRouteGPX")
    val racerSpeeds = routeGPX!!.getSpeed()
    val makerSpeeds = mapRouteGPX!!.getSpeed()
    Logg.d("ssmm11 reuslt = $result")

    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          GETMAKERDATA -> {
            makerData = msg.obj as InfoData
          }


          GETRACING -> {
            arrRankingData = msg.obj as ArrayList<RankingData>
            val resultRankText = msg.arg1
            setUiData(racerSpeeds, makerSpeeds, resultRankText)

            // Recycler view adpater 추가
            //resultPlayerRankingRecycler.layoutManager = LinearLayoutManager(baseContext)
            //resultPlayerRankingRecycler.adapter = RankRecyclerViewAdapterTopPlayer(arrRankingData, racerData.mapTitle!!)

          }
        }
      }
    }

    // 메이커 인포데이터를 가져오는 함수
    FBRacingRepository().getMakerData(racerData, mHandler)

    // 유저 인포에 해당 유저가 이 맵을 뛰었다는
    // 히스토리를 더하는 함수
    FBRacingRepository().setUserInfoRacing(racerData)


    FBRacingRepository().setRankingData(result, racerData, mHandler)

    OKButton.setOnClickListener(this)
    otherPeopleProfileSelect.setOnClickListener(this)
  }



  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.OKButton -> {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
      }

      //리스트 선택화면으로 넘어감
      R.id.otherPeopleProfileSelect->{
        val intent = Intent(this, AllRanking::class.java)
        startActivity(intent)
      }

    }
  }


  private fun RouteGPX.getSpeed(): MutableList<Double> {
    val speeds = mutableListOf<Double>()
    trkList.forEach {
      speeds.add(it.speed.get().toDouble())
    }
    return speeds
  }

  private fun setUiData(
    racerSpeeds: MutableList<Double>,
    makerSpeeds: MutableList<Double>,
    resultRankText: Int
  ) {

    if (resultRankText == 0) {
      resultRankTextView.text = getString(R.string.fail)
    } else {
      resultRankTextView.text = resultRankText.toRank()
    }

    makerLapTimeTextView.text = makerData.time!!.format(MM_SS)
    makerMaxSpeedTextView.text = makerSpeeds.max()!!.prettyDistance()
    makerAvgSpeedTextView.text = makerSpeeds.average().prettyDistance()

    racerLapTimeTextView.text = racerData.time!!.format(MM_SS)
    racerMaxSpeedTextView.text = racerSpeeds.max()!!.prettyDistance()
    racerAvgSpeedTextView.text = racerSpeeds.average().prettyDistance()
    progressbar.dismiss()
  }


}
