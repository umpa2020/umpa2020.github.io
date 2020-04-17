package com.umpa2020.tracer.main.start.racing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.MM_SS
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.extensions.toRank
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.network.RacingFinishListener
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*


class RacingFinishActivity : AppCompatActivity(), OnSingleClickListener {
  val GETMAKERDATA = 100

  var activity = this
  lateinit var racerData: InfoData
  lateinit var makerData: InfoData
  var arrRankingData: ArrayList<RankingData> = arrayListOf()
  lateinit var progressbar: ProgressBar

  var racerSpeeds = mutableListOf<Double>()
  var makerSpeeds = mutableListOf<Double>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_finish)

    progressbar = ProgressBar(this)
    progressbar.show()

    // Racing Activity 에서 넘겨준 infoData를 받아서 활용
    racerData = intent.getParcelableExtra("InfoData") as InfoData
    val result = intent.extras!!.getBoolean("Result")
    val routeGPX = intent.getParcelableExtra<RouteGPX>("RouteGPX")
    val mapRouteGPX = intent.getParcelableExtra<RouteGPX>("MapRouteGPX")
    racerSpeeds = routeGPX!!.getSpeed()
    makerSpeeds = mapRouteGPX!!.getSpeed()

    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          GETMAKERDATA -> {
            makerData = msg.obj as InfoData
          }
        }
      }
    }

    // 메이커 인포데이터를 가져오는 함수
    FBRacingRepository().getMakerData(racerData, mHandler)

    // 유저 인포에 해당 유저가 이 맵을 뛰었다는
    // 히스토리를 더하는 함수
    FBRacingRepository().setUserInfoRacing(racerData)

    FBRacingRepository().setRankingData(result, racerData, mHandler, racingFinishListener)

    OKButton.setOnClickListener(this)
    otherPeopleProfileSelect.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.OKButton -> {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
      }

      //리스트 선택화면으로 넘어감
      R.id.otherPeopleProfileSelect->{
        val intent = Intent(this, AllRankingActivity::class.java)
        intent.putExtra("arrRankingData", arrRankingData)
        startActivityForResult(intent, 100)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == 100) {
      if (resultCode == 100) {
        val getNickname = data!!.getStringExtra("result")
        RacingFinishAnalysisOtherNickname.text = getNickname

        progressbar.show()

        val db = FirebaseFirestore.getInstance()
        var profileImagePath = "init"
        db.collection("userinfo").whereEqualTo("nickname", getNickname)
          .get()
          .addOnSuccessListener { result ->
            for (document in result) {
              profileImagePath = document.get("profileImagePath") as String
              break
            }
            // glide imageview 소스
            // 프사 설정하는 코드 db -> imageView glide
            val storage = FirebaseStorage.getInstance()
            val profileRef = storage.reference.child(profileImagePath)

            profileRef.downloadUrl.addOnCompleteListener { task ->
              if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Glide.with(App.instance.currentActivity() as Activity)
                  .load(task.result)
                  .override(1024, 980)
                  .into(RacingFinishAnalysisOtherProfile)
                progressbar.dismiss()
              } else {
                progressbar.dismiss()
              }
            }
          }
        //FBProfileRepository().getProfileImage(RacingFinishAnalysisOtherProfile, getNickname!!)
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun RouteGPX.getSpeed(): MutableList<Double> {
    val speeds = mutableListOf<Double>()
    trkList.forEach {
      speeds.add(it.speed.get().toDouble())
    }
    return speeds
  }

  private fun setMyUiData(
    racerSpeeds: MutableList<Double>,
    makerSpeeds: MutableList<Double>,
    resultRankText: Int
  ) {

    // 나의 기록
    FBProfileRepository().getProfileImage(racingFinishProfileImageView, UserInfo.nickname)
    RacingFinishMyNickName.text = UserInfo.nickname

    if (resultRankText == 0) {
      resultRankTextView.text = getString(R.string.fail)
    } else {
      resultRankTextView.text = resultRankText.toRank()
    }

    RacingFinishMyLapTime.text = makerData.time!!.format(MM_SS)

    FBProfileRepository().getProfileImage(RacingFinishAnalysisMyProfile, UserInfo.nickname)
    RacingFinishAnalysisMyNickname.text = UserInfo.nickname

    // maker data

    FBProfileRepository().getProfileImage(RacingFinishAnalysisOtherProfile, makerData.makersNickname!!)
    RacingFinishAnalysisOtherNickname.text = makerData.makersNickname

    makerLapTimeTextView.text = makerData.time!!.format(MM_SS)
    makerMaxSpeedTextView.text = makerSpeeds.max()!!.prettyDistance()
    makerAvgSpeedTextView.text = makerSpeeds.average().prettyDistance()

    // temp

    racerSpeeds.add(3.0)
    racerSpeeds.add(4.0)
    racerSpeeds.add(5.0)
    racerSpeeds.add(6.0)
    racerSpeeds.add(7.0)

    // temo

    racerLapTimeTextView.text = racerData.time!!.format(MM_SS)
    racerMaxSpeedTextView.text = racerSpeeds.max()!!.prettyDistance()
    racerAvgSpeedTextView.text = racerSpeeds.average().prettyDistance()
    progressbar.dismiss()
  }

  private fun setOtherData() {

  }

  private val racingFinishListener = object : RacingFinishListener {
    override fun getRacingFinish(rankingDatas: ArrayList<RankingData>, resultRank: Int) {
      arrRankingData = rankingDatas
      setMyUiData(racerSpeeds, makerSpeeds, resultRank)

      if (arrRankingData.size >= 1) {
        FBProfileRepository().getProfileImage(racingFinishProfileFirst, arrRankingData[0].challengerNickname!!)
        racingFinishNicknameFirst.text = arrRankingData[0].challengerNickname
        racingFinishLapTimeFirst.text = arrRankingData[0].challengerTime!!.format(MM_SS)
      }

      if (arrRankingData.size >= 2) {
        FBProfileRepository().getProfileImage(racingFinishProfileSecond, arrRankingData[1].challengerNickname!!)
        racingFinishNicknameSecond.text = arrRankingData[1].challengerNickname
        racingFinishLapTimeSecond.text = arrRankingData[1].challengerTime!!.format(MM_SS)
      }

      if (arrRankingData.size >= 3) {
        FBProfileRepository().getProfileImage(racingFinishProfileThird, arrRankingData[2].challengerNickname!!)
        racingFinishNicknameThird.text = arrRankingData[2].challengerNickname
        racingFinishLapTimeThird.text = arrRankingData[2].challengerTime!!.format(MM_SS)
      }
    }

    override fun getOtherRacing(otherData: RankingData) {

    }
  }
}
