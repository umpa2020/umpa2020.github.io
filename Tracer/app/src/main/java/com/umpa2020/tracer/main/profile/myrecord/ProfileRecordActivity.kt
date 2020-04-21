package com.umpa2020.tracer.main.profile.myrecord

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.network.ActivityListener
import com.umpa2020.tracer.network.FBUserActivityRepository
import kotlinx.android.synthetic.main.activity_profile_record.*

class ProfileRecordActivity : AppCompatActivity() {
  val activity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_record)

    FBUserActivityRepository().getUserMakingActivity(activityListener)
  }

  private val activityListener = object : ActivityListener {
    override fun activityList(activityData: ArrayList<ActivityData>) {
      //adpater 추가
      profileRecyclerRecord.layoutManager = LinearLayoutManager(activity)
      profileRecyclerRecord.adapter = ProfileRecyclerViewAdapterRecord(activityData)
    }
  }
}
