package com.umpa2020.tracer.main.profile.myroute

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.network.FBProfile
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_profile_route.*

class ProfileRouteActivity : AppCompatActivity() {

  val MYROUTE = 60
  val MYROUTEFAIL = 70
  lateinit var getinfoDatas: ArrayList<InfoData>
  val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
  var nickname = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_route)

    // 프로그래스 바 띄우기
    progressbar.show()

    val intent = intent
    //전달 받은 값으로 Title 설정
    nickname = intent.extras?.getString("nickname").toString()

    // 나의 루트가 아닌 다른 사람의 루트를 볼 경우
    // 텍스트 변한
    if (nickname != UserInfo.nickname) {
      profileRouteMyRoute.text = getString(R.string.other_route)
    }
  }

  override fun onResume() {
    // 마이 루트에 필요한 내용을 받아옴
    FBProfile().getRoute(mHandler, nickname)
    super.onResume()
  }

  /**
   * 핸들러로 받아온 루트 데이터들을
   * 리사이클러뷰에 띄워줌
   */
  val mHandler = object : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
      when (msg.what) {
        MYROUTE -> {
          getinfoDatas = msg.obj as ArrayList<InfoData>
          //adpater 추가
          profileRecyclerRoute.adapter = ProfileRecyclerViewAdapterRoute(getinfoDatas)
          profileRecyclerRoute.layoutManager = LinearLayoutManager(App.instance)
          profileRecyclerRouteisEmpty.visibility = View.GONE
          progressbar.dismiss()
        }
        MYROUTEFAIL -> {
          profileRecyclerRouteisEmpty.visibility = View.VISIBLE
          progressbar.dismiss()
        }
      }
    }
  }
}