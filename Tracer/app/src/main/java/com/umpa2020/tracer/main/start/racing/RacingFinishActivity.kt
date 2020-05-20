package com.umpa2020.tracer.main.start.racing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class RacingFinishActivity : AppCompatActivity(), OnSingleClickListener {

  var activity = this
  lateinit var racerData: MapInfo
  lateinit var arrRankingData: MutableList<RankingData>
  lateinit var progressbar: ProgressBar

  var racerSpeeds = mutableListOf<Double>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_finish)

    progressbar = ProgressBar(this)
    progressbar.show()

    // Racing Activity 에서 넘겨준 infoData를 받아서 활용
    racerData = intent.getParcelableExtra("InfoData") as MapInfo
    val result = intent.extras!!.getBoolean("Result")
    val racerGPX = intent.getParcelableExtra<RouteGPX>("RouteGPX")
    racerSpeeds = racerGPX!!.getSpeed()
    val rankingData = RankingData(
      racerData.makerId,
      UserInfo.autoLoginKey,
      UserInfo.nickname,
      racerData.time,
      false,
      racerSpeeds.max().toString(),
      racerSpeeds.average().toString(),
      null
    )

    val saveFolder = File(App.instance.filesDir, "routeGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val racerGpxFile = racerGPX.classToGpx(saveFolder.path)

    MainScope().launch {
      // 유저 히스토리 등록
      FBUsersRepository().createUserHistory(
        ActivityData(racerData.mapId, Date().time, racerData.distance, racerData.time, if (result) BaseFB.ActivityMode.RACING_SUCCESS else BaseFB.ActivityMode.RACING_FAIL)
      )

      FBMapRepository().getMapInfo(racerData.mapId).let {
        FBAchievementRepository().incrementPlays(it!!.makerId)
      }

      //성공했다면 랭킹에 등록
      if (result)
        FBRacingRepository().createRankingData(racerData, rankingData, racerGpxFile)

      arrRankingData = FBMapRepository().listMapRanking(racerData.mapId)
      FBUsersRepository().updateUserAchievement(arrRankingData, racerData.mapId)
      updateRankingUI(arrRankingData)
      progressbar.dismiss()
    }
    OKButton.setOnClickListener(this)
    otherPeopleProfileSelect.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      // 다 봤다는 표시 - 그래도 앞에 있던 액티비티들을 끄고, 메인 엑티비티 실행
      R.id.OKButton -> {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
      }

      // 리스트 선택화면으로 넘어감
      R.id.otherPeopleProfileSelect -> {
        val intent = Intent(this, AllRankingActivity::class.java)
        intent.putParcelableArrayListExtra("arrRankingData", ArrayList(arrRankingData))
        startActivityForResult(intent, 100)
      }
    }
  }

  /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      if (resultCode == 100) {
        if (resultCode == 100) {
          val getNickname = data!!.getStringExtra("result")
          RacingFinishAnalysisOtherNickname.text = getNickname

          progressbar.show()
          FBRacingRepository().getOtherData(racerData.mapTitle!!, getNickname!!)
          /**
           * TODO 아래 코드 원래 있던 코드를 재활용 안하고 새로 했는데 - 정빈
           *
           * 1. AllRankingActivity 안에 RecyclerView에서 종료를 시키고 있어서
           *    제대로 된 종료가 안되서
           * 2. 원래 있던 코드를 사용하면 APP.instance 부분이 오류가 나게 됨
           *
           */
          val db = FirebaseFirestore.getInstance()
          var profileImagePath = "init"
          db.collection(USERS).whereEqualTo("nickname", getNickname)
            .get()
            .addOnSuccessListener { result ->
              if (!result.isEmpty) {
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
                    RacingFinishAnalysisOtherProfile.image(task.result!!)
                    progressbar.dismiss()
                  } else {
                    progressbar.dismiss()
                  }
                }
              } else {
                RacingFinishAnalysisOtherNickname.text = "탈퇴한 회원입니다."
              }

            }
          //FBProfileRepository().getProfileImage(RacingFinishAnalysisOtherProfile, getNickname!!)
        }
      }
      super.onActivityResult(requestCode, resultCode, data)
    }

  */
  @SuppressLint("ShowToast")
  private fun setMyUiData(
    racerSpeeds: MutableList<Double>,
    resultRankText: Int,
    renewal: Boolean
  ) {
    MainScope().launch {
      // 나의 기록
      FBProfileRepository().getProfileImage(UserInfo.autoLoginKey)?.let { racingFinishProfileImageView.image(it) }
      //RacingFinishMyNickName.text = UserInfo.nickname

      if (resultRankText == 0) {
        resultRankTextView.text = getString(R.string.fail)
      } else {
        if (renewal) {
          getString(R.string.renewal).show()
          resultRankTextView.text = resultRankText.toRank()
        } else
          resultRankTextView.text = resultRankText.toRank()
      }

      RacingFinishMyLapTime.text = racerData.time.format(m_s)

      FBProfileRepository().getProfileImage(UserInfo.autoLoginKey)?.let { RacingFinishAnalysisMyProfile.image(it) }
      RacingFinishAnalysisMyNickname.text = UserInfo.nickname

      racerLapTimeTextView.text = racerData.time.format(m_s)
      racerMaxSpeedTextView.text = racerSpeeds.max()!!.prettyDistance
      racerAvgSpeedTextView.text = racerSpeeds.average().prettyDistance
      progressbar.dismiss()
    }
  }

  private suspend fun updateRankingUI(rankingDatas: MutableList<RankingData>) {
    var resultRank = 1
    var renewal = false
    rankingDatas.forEach {
      if (racerData.time > it.challengerTime!!) {
        if (UserInfo.autoLoginKey == it.challengerId) {
          renewal = true
        }
        resultRank++
      }

      arrRankingData = rankingDatas
      setMyUiData(racerSpeeds, resultRank, renewal)

      if (arrRankingData.size >= 1) {
        FBProfileRepository().getProfileImage(arrRankingData[0].challengerId!!)
          ?.let { racingFinishProfileFirst.image(it) }
        racingFinishNicknameFirst.text = arrRankingData[0].challengerNickname
        racingFinishLapTimeFirst.text = arrRankingData[0].challengerTime!!.format(m_s)

        FBProfileRepository().getProfileImage(arrRankingData[0].challengerId!!)
          ?.let { RacingFinishAnalysisOtherProfile.image(it) }

        RacingFinishAnalysisOtherNickname.text = arrRankingData[0].challengerNickname
        otherLapTimeTextView.text = arrRankingData[0].challengerTime!!.format(m_s)
        otherMaxSpeedTextView.text = arrRankingData[0].maxSpeed!!.toDouble().prettyDistance
        otherAvgSpeedTextView.text = arrRankingData[0].averageSpeed!!.toDouble().prettyDistance
      }

      if (arrRankingData.size >= 2) {
        FBProfileRepository().getProfileImage(arrRankingData[1].challengerId!!)
          ?.let { racingFinishProfileSecond.image(it) }
        racingFinishNicknameSecond.text = arrRankingData[1].challengerNickname
        racingFinishLapTimeSecond.text = arrRankingData[1].challengerTime!!.format(m_s)
      }

      if (arrRankingData.size >= 3) {
        FBProfileRepository().getProfileImage(arrRankingData[2].challengerId!!)
          ?.let { racingFinishProfileThird.image(it) }
        racingFinishNicknameThird.text = arrRankingData[2].challengerNickname
        racingFinishLapTimeThird.text = arrRankingData[2].challengerTime!!.format(m_s)
      }
    }
  }
}
