package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.util.UserInfo
import java.util.*

class FBRacingRepository {
  val GETMAKERDATA = 100
  val GETRACING = 101
  val db = FirebaseFirestore.getInstance()


  /**
   * 첫 번째 실행 되어야하는 함수
   * 현재 유저와 다른 레이서들의 기록을 비교하여
   * 최고 기록일 경우 bestTime을 1로 설정하고
   * 최고 기록이 아닐 경우 bestTime을 0으로 설정하여
   * ranking 에 등록하는 함수
   */
  fun setRankingData(result: Boolean, racerData: InfoData, mHandler: Handler) {
    // 타임스탬프
    val timestamp = Date().time

    if (result) {
      val rankingData = RankingData(racerData.makersNickname, UserInfo.nickname, racerData.time, 1)
      // 랭킹 맵에서
      db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking")
        .whereEqualTo("bestTime", 1)
        .get()
        .addOnSuccessListener {
          for (document in it) {
            if (document.get("challengerNickname") == UserInfo.nickname) {
              if (racerData.time!!.toLong() < document.get("challengerTime") as Long) {
                db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking")
                  .document(document.id).update("bestTime", 0)
              } else {
                rankingData.bestTime = 0
              }
            }
          }
          db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking")
            .document(UserInfo.autoLoginKey + timestamp).set(rankingData)

          getRacingFinishRank(result, racerData, mHandler)
        }
    } else {
      getRacingFinishRank(result, racerData, mHandler)
    }
  }

  /**
   * 랭킹맵에서 올라와 있는 기록들을
   * 1. 동일한 유저의 기록은 최고 기록만 저장
   * 2. 성공했을 때에만 N등
   * 3. 실패했을 경우 실패
   */
  private fun getRacingFinishRank(result: Boolean, racerData: InfoData, mHandler: Handler) {
    val arrRankingData: ArrayList<RankingData> = arrayListOf()
    var arg = 0

    db.collection("rankingMap").document(racerData.mapTitle!!).collection("ranking").orderBy("challengerTime", Query.Direction.ASCENDING)
      .get()
      .addOnSuccessListener { resultdb ->
        var index = 1
        for (document2 in resultdb) {
          val recycleRankingData: RankingData = document2.toObject(RankingData::class.java)
          if (recycleRankingData.bestTime == 1) {
            if (document2.get("challengerNickname") == UserInfo.nickname) {
              if (result) {
                arg = index
              }
            }
            //최대 10위까지만 띄우기
            if (arrRankingData.size > 10) {
              break
            }
            arrRankingData.add(recycleRankingData)
            index++
          }
        }
        val msg: Message = mHandler.obtainMessage(GETRACING)
        msg.obj = arrRankingData
        msg.arg1 = arg
        mHandler.sendMessage(msg)
      }
  }

  /**
   * 메이커 인포데이터를 가져오는 함수
   */
  fun getMakerData(racerData: InfoData, mHandler: Handler) {
    lateinit var makerData: InfoData

    db.collection("mapInfo").document(racerData.mapTitle!!)
      .get()
      .addOnSuccessListener { document ->
        makerData = document.toObject(InfoData::class.java)!!
        val msg: Message = mHandler.obtainMessage(GETMAKERDATA)
        msg.obj = makerData
        mHandler.sendMessage(msg)
      }
  }

  /**
   * 유저 인포에 해당 유저가 이 맵을 뛰었다는
   * 히스토리를 더하는 함수
   */
  fun setUserInfoRacing(racerData: InfoData) {
    val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
    db.collection("userinfo").document(UserInfo.autoLoginKey).collection("user ran these maps").add(ranMapsData)
  }
}