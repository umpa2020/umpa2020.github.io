package com.umpa2020.tracer.main.profile.myachievement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.TrophyData
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.BaseFB.Companion.DISTANCE
import com.umpa2020.tracer.network.FBProfileRepository
import kotlinx.android.synthetic.main.activity_profile_achivement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 나의 업적 액티비티
 */
class ProfileAchievementActivity : AppCompatActivity(), CoroutineScope by MainScope() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_achivement)

    profileRecyclerAchievement.layoutManager = GridLayoutManager(baseContext, 3)
    //profileRecyclerAchievement.adapter = ProfileRecyclerViewAdapterAchievement()

    val uid = intent.extras?.getString(BaseFB.USER_ID).toString()
    val distance = intent.extras?.getDouble(DISTANCE)
    launch {
      FBProfileRepository().getCountMap(uid).let {
      }
    }
    launch {
      FBProfileRepository().getCelebrity(uid).let {
      }
    }


  }

  private fun achievementDataList(listAchievementData: MutableList<TrophyData>) {
//    profileRecyclerAchievement.layoutManager = GridLayoutManager(context, 3)
  }
}
