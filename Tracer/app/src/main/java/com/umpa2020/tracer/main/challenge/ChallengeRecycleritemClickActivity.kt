package com.umpa2020.tracer.main.challenge

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.OnSingleClickListener

class ChallengeRecycleritemClickActivity : AppCompatActivity(), OnSingleClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_map_detail)
  }

  override fun onSingleClick(v: View?) {
  }
}

