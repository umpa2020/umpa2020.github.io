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

class ChallengeRecycleritemClickActivity : AppCompatActivity(), OnSingleClickListener {

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_map_detail)

    val challengeId = intent.getStringExtra("challengeId")!!


    MainScope().launch {
      FBChallengeRepository().getChallengeData(challengeId).let {
        challengeDetailImageView.image(FBStorageRepository().downloadFile(it.imagePath!!))
        challengeDetailCompetitionName.text = it.name
        challengeDetailCompetitionDate.text = it.date!!.format(Y_M_D)
        challengeDetailCompetitionPeriod.text = it.from!!.format(Y_M_D) + " ~ " + it.to!!.format(Y_M_D)
        challengeDetailAddress.text = it.address
        challengeDetailHost.text = it.host
        challengeDetailInformation.text = it.intro
      }
    }
    
  }

  override fun onSingleClick(v: View?) {
  }

}

