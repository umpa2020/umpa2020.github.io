package com.umpa2020.tracer.main.profile.myachievement

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.EmblemData
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.FBAchievementRepository
import kotlinx.android.synthetic.main.activity_profile_achivement.*
import kotlinx.coroutines.launch

/**
 * 나의 업적 액티비티
 */
class ProfileAchievementActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_achivement)


    val uid = intent.extras?.getString(BaseFB.USER_ID).toString()

    launch {
      FBAchievementRepository().listUserEmblemNames(uid).let {
        FBAchievementRepository().listEmblemImagePaths(it).let {
          achievementDataList(it)
        }
      }
    }
  }

  private fun achievementDataList(imagePaths: MutableList<EmblemData>) {
    profileRecyclerAchievement.layoutManager = GridLayoutManager(baseContext, 3)
    profileRecyclerAchievement.adapter = ProfileRecyclerViewAdapterAchievement(imagePaths)
  }
}
