package com.umpa2020.tracer.main.challenge

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.network.FBChallengeRepository
import com.umpa2020.tracer.network.FBStorageRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_challenge_map_detail.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 하나의 대회를 선택하면 해당 대회의 정보를
 * 자세히 보여주는 액티비티, 추 후에 뛸 수 있도록 연동
 */
class ChallengeRecycleritemClickActivity : AppCompatActivity(), OnSingleClickListener {

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_map_detail)

    val challengeId = intent.getStringExtra("challengeId")!!

    MainScope().launch {
      FBChallengeRepository().getChallengeData(challengeId).run {
        challengeDetailImageView.image(FBStorageRepository().downloadFile(imagePath!!))
        challengeDetailCompetitionName.text = name
        challengeDetailCompetitionDate.text = date!!.format(Y_M_D)
        challengeDetailCompetitionPeriod.text = from!!.format(Y_M_D) + " ~ " + to!!.format(Y_M_D)
        challengeDetailAddress.text = address
        challengeDetailHost.text = host
        challengeDetailInformation.text = intro
      }
    }
  }

  override fun onSingleClick(v: View?) {
  }
}

