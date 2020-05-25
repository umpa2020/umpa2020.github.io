package com.umpa2020.tracer.main.profile.myActivity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.network.BaseFB
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_profile_record.*
import kotlinx.coroutines.launch

/**
 * 나의 활동 기록 액티비티
 * 1. 내가 맵을 만들었는지
 * 2. 레이싱 완주했는지
 * 3. 레이싱 도중 포기했는지 맵 이미지와 함께 표기
 */
class ProfileActivityActivity : BaseActivity() {
  val rootActivityDatas = arrayListOf<ActivityData>()
  val progressbar = MyProgressBar()
  var isLoading = false
  var userActivityRepo = FBUsersRepository()
  var limit = 0L
  var userId = ""

  /**
   * 순서
   * 1. Resume 에서 값을 받아오고
   * 2. 리사이클러뷰에 끝에 도달하면 더 가져오기
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_record)


    userId = intent.getStringExtra(BaseFB.USER_ID)!!
    progressbar.show()
    profileRecyclerRecord.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!profileRecyclerRecord.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!profileRecyclerRecord.canScrollVertically(1)) {
          /* 리사이클러뷰가 맨 아래로 이동했을 경우 */
          if (!isLoading) {
            launch {
              userActivityRepo.listUserMakingActivity(userId, 15)?.let {
                activityList(it)
              }
            }
          }
          isLoading = true
        }
      }
    })
  }

  override fun onResume() {
    limit = rootActivityDatas.size.toLong()

    if (limit == 0L) limit = 15L
    else rootActivityDatas.clear()

    /**
     * 나의 활동 리스트를 가져오기
     */

    launch {
      userActivityRepo = FBUsersRepository()
      userActivityRepo.listUserMakingActivity(userId, limit)?.let {
        activityList(it)
      } ?: kotlin.run {
        activityList(rootActivityDatas)
      }
    }
    super.onResume()
  }

  /**
   * 값을 받아온 뒤, 전역 list 에서 값을 바꾸고
   * 알림을 주기
   */
  fun activityList(activityDatas: List<ActivityData>) {
    rootActivityDatas.addAll(activityDatas)
    if (rootActivityDatas.isEmpty()) {
      profileRecyclerActivityisEmpty.visibility = View.VISIBLE
      progressbar.dismiss()
    } else {
      if (rootActivityDatas.size < 16) {
        //adpater 추가
        profileRecyclerRecord.adapter = ProfileRecyclerViewAdapterRecord(rootActivityDatas)
        profileRecyclerRecord.layoutManager = LinearLayoutManager(this)
        profileRecyclerActivityisEmpty.visibility = View.GONE
      } else {
        profileRecyclerRecord.adapter!!.notifyDataSetChanged()
      }
      isLoading = false
      progressbar.dismiss()
    }
  }
}
