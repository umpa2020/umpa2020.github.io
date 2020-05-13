package com.umpa2020.tracer.main.profile.myrecord

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_profile_record.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileRecordActivity : AppCompatActivity() {
  val activity = this
  val rootActivityDatas = arrayListOf<ActivityData>()
  val progressbar = MyProgressBar()
  var isLoding = false
  val userActivityRepo = FBUsersRepository()
  var limit = 0L


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_record)

    progressbar.show()
    profileRecyclerRecord.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!profileRecyclerRecord.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!profileRecyclerRecord.canScrollVertically(1)) {
          /* 리사이클러뷰가 맨 아래로 이동했을 경우 */
          if (!isLoding) {
            MainScope().launch {
              userActivityRepo.listUserMakingActivity(15)?.let {
                activityList(it)
              }
            }
          }
          isLoding = true
        }
      }
    })
  }

  override fun onResume() {
    limit = rootActivityDatas.size.toLong()

    if (limit == 0L) limit = 15L
    else rootActivityDatas.clear()

    MainScope().launch {
      userActivityRepo.listUserMakingActivity(limit)?.let {
        activityList(it)
      }
    }
    super.onResume()
  }

  fun activityList(activityDatas: List<ActivityData>) {
    rootActivityDatas.addAll(activityDatas)
    Logg.d("ssmm11 size = ${activityDatas.size}")
    if (rootActivityDatas.isEmpty()) {
      profileRecyclerActivityisEmpty.visibility = View.VISIBLE
      progressbar.dismiss()
    } else {
      if (rootActivityDatas.size < 16) {
        //adpater 추가
        profileRecyclerRecord.adapter = ProfileRecyclerViewAdapterRecord(rootActivityDatas)
        profileRecyclerRecord.layoutManager = LinearLayoutManager(activity)
        profileRecyclerActivityisEmpty.visibility = View.GONE
      } else {
        profileRecyclerRecord.adapter!!.notifyDataSetChanged()
      }
      isLoding = false
      progressbar.dismiss()
    }
  }
}
