package com.umpa2020.tracer.main.profile.myachievement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.AchievementData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.main.challenge.ChallengeRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_profile_achivement.*
import kotlinx.android.synthetic.main.fragment_challenge.*

/**
 * 나의 업적 액티비티
 */
class ProfileAchievementActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_achivement)


  }

  private fun achievementDataList(listAchievementData: MutableList<AchievementData>) {
//    profileRecyclerAchievement.layoutManager = GridLayoutManager(context, 3)
  }
}
