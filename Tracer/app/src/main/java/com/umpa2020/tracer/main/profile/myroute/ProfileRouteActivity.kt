package com.umpa2020.tracer.main.profile.myroute

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ID
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile_route.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileRouteActivity : AppCompatActivity() {
  val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
  var uid = ""
  var nickname = ""
  var isLoding = false
  val rootInfoDatas = arrayListOf<MapInfo>()
  var limit = 0L
  val repository = FBUsersRepository()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_route)

    progressbar.show()
    uid = intent.extras?.getString(USER_ID).toString()

    profileRecyclerRoute.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!profileRecyclerRoute.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!profileRecyclerRoute.canScrollVertically(1)) {
          // 리사이클러뷰가 맨 아래로 이동했을 경우
          if (!isLoding) {
            MainScope().launch {
              listProfileRoute(repository.listUserRoute(uid, 5))
            }
          }
          isLoding = true
        }
      }
    })

    // 나의 루트가 아닌 다른 사람의 루트를 볼 경우
    // 텍스트 변한
    if (uid != UserInfo.autoLoginKey) {
      profileRouteMyRoute.text = getString(R.string.other_route)
    }
  }

  override fun onResume() {
    // 마이 루트에 필요한 내용을 받아옴
    limit = rootInfoDatas.size.toLong()
    Logg.d("ssmm11 limit = $limit")
    if (limit == 0L) limit = 5L
    else rootInfoDatas.clear()
    MainScope().launch {
      listProfileRoute(FBUsersRepository().listUserRoute(uid, limit))
    }
    super.onResume()
  }

  /**
   * 리스너로 받아온 루트 데이터들을
   * 리사이클러뷰에 띄워줌
   */
  fun listProfileRoute(mapInfos: List<MapInfo>?) {
    if (mapInfos != null)
      rootInfoDatas.addAll(mapInfos)

    Logg.d("ssmm11 size = ${rootInfoDatas.size}")


    if (rootInfoDatas.isEmpty()) {
      profileRecyclerRouteisEmpty.visibility = View.VISIBLE
      progressbar.dismiss()
    } else {
      if (rootInfoDatas.size < 6) {
        profileRecyclerRoute.adapter = ProfileRouteRecyclerViewAdapter(rootInfoDatas)
        profileRecyclerRoute.layoutManager = LinearLayoutManager(App.instance)
        profileRecyclerRouteisEmpty.visibility = View.GONE
      } else {
        profileRecyclerRoute.adapter!!.notifyDataSetChanged()
      }
      isLoding = false
      progressbar.dismiss()
    }
  }
}