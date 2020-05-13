package com.umpa2020.tracer.main.profile.myroute

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUsersRepository
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
  val routeRepo = FBProfileRepository()
  val rootInfoDatas = arrayListOf<InfoData>()
  var limit = 0L


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_route)

    // 프로그래스 바 띄우기
    progressbar.show()

    val intent = intent
    //전달 받은 값으로 Title 설정
    uid = intent.extras?.getString("UID").toString()

    profileRecyclerRoute.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!profileRecyclerRoute.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!profileRecyclerRoute.canScrollVertically(1)) {
          // 리사이클러뷰가 맨 아래로 이동했을 경우
          if (!isLoding) {
            MainScope().launch {
              listProfileRoute(FBUsersRepository().listUserRoute(uid, 5))
            }

          }
          isLoding = true
        }
      }
    })

    // 나의 루트가 아닌 다른 사람의 루트를 볼 경우
    // 텍스트 변한
    if (uid != UserInfo.nickname) {
      profileRouteMyRoute.text = getString(R.string.other_route)
    }
  }

  override fun onResume() {
    // 마이 루트에 필요한 내용을 받아옴
    limit = rootInfoDatas.size.toLong()

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
  fun listProfileRoute(infoDatas: List<InfoData>) {
      rootInfoDatas.addAll(infoDatas)

      if (rootInfoDatas.isEmpty()) {
        profileRecyclerRouteisEmpty.visibility = View.VISIBLE
        progressbar.dismiss()
      } else {
        if (rootInfoDatas.size < 6) {
          profileRecyclerRoute.adapter = ProfileRecyclerViewAdapterRoute(rootInfoDatas)
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