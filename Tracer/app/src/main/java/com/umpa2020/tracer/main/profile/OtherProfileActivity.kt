package com.umpa2020.tracer.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.network.FBProfile
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_other_profile.*
import org.jetbrains.anko.contentView

class OtherProfileActivity : AppCompatActivity(), OnSingleClickListener {

  var nickname = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_other_profile)

    val intent = intent
    //전달 받은 값으로 Title 설정
    val mapTitle = intent.extras?.getString("mapTitle").toString()
    nickname = intent.extras?.getString("nickname").toString()

    // 넘어온 닉네임으로 현재 액티비티 닉네임 적용
    profileIdTextView.text = nickname

    FBProfile().setProfile(contentView!!, nickname)

    otherProfileRouteTextView.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when(v!!.id){
      R.id.otherProfileRouteTextView->{
        // 루트 클릭하면 해당 사용자가 만든 루트 볼 수 잇는 페이지로 이동
        val nextIntent = Intent(this, ProfileRouteActivity::class.java)
        nextIntent.putExtra("nickname", nickname)
        startActivity(nextIntent)
      }
    }
  }
}
