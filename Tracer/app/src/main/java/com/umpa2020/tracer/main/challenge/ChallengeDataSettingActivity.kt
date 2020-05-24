package com.umpa2020.tracer.main.challenge

import android.os.Bundle
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.network.FBChallengeRepository
import com.umpa2020.tracer.network.FBMapRepository
import kotlinx.android.synthetic.main.activity_challenge_data_setting.*
import java.util.*

/**
 * 챌린지 데이터 셋팅 액티비티, 등록된 값을 넣어주기만 함.
 */
class ChallengeDataSettingActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_data_setting)
    intent.getStringExtra("challengeId")?.let {
      challenge1.hint = it
    }

    val timeStamp = Date().time
    challengeButton.setOnClickListener {
      val cutted = challenge12.text.toString().split(",").toMutableList()
      val cuttedLocale = challenge5.textLocale.toString().split(" ").toMutableList()

      val challengeData = ChallengeData(
        "${challenge1.text}$timeStamp",
        "${challenge1.text}",
        challenge2.text.toString().toLong(),
        challenge3.text.toString().toLong(),
        challenge4.text.toString().toLong(),
        cuttedLocale,
        "${challenge6.text}",
        "${challenge7.text}",
        "${challenge8.text}",
        "${challenge9.text}",
        "${challenge10.text}",
        "${challenge11.text}",
        cutted,
        "challenge/$timeStamp/$timeStamp.jpg"
      )

      FBChallengeRepository().createChallengeData(challengeData)

      val mapInfo = MapInfo(
        challengeData.id!!, challengeData.name!!, challengeData.host!!, "", "", "mapRoute/${challengeData.id}/${challengeData.id}",
        0.0, 0, 0, 0, 0.0, 0.0, false, false, 0.0, 0.0, 0L, true
      )

      FBMapRepository().mapsCollectionRef.document(mapInfo.mapId).set(mapInfo)
    }
  }
}
