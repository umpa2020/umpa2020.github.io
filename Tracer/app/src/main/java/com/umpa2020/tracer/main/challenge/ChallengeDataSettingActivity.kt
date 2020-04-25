package com.umpa2020.tracer.main.challenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.network.FBChallengeRepository
import kotlinx.android.synthetic.main.activity_challenge_data_setting.*
import java.util.*

class ChallengeDataSettingActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_data_setting)
    intent.getStringExtra("challengeId")?.let {
      challenge1.hint=it
    }

    val dt = Date()
    val timeStamp = dt.time
    challengeButton.setOnClickListener {

      val cutted = challenge12.text.toString().split(",").toMutableList()

      val challengeData = ChallengeData(
        "${challenge1.text}+$timeStamp",
        "${challenge1.text}",
        "${challenge2.text}",
        "${challenge3.text}",
        "${challenge4.text}",
        "${challenge5.text}",
        "${challenge6.text}",
        "${challenge7.text}",
        "${challenge8.text}",
        "${challenge9.text}",
        "${challenge10.text}",
        "${challenge11.text}",
        cutted,
        "Challenge/$timeStamp",
        "Challenge/$timeStamp/$timeStamp.jpg"
      )


      FBChallengeRepository().createChallengeData(challengeData)




    }


  }
}
