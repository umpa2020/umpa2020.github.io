package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.network.BaseFB.Companion.BEST_TIME
import com.umpa2020.tracer.network.BaseFB.Companion.CHALLENGER_TIME
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_INFO
import com.umpa2020.tracer.network.BaseFB.Companion.RANKING
import com.umpa2020.tracer.network.BaseFB.Companion.RANKING_MAP
import com.umpa2020.tracer.network.BaseFB.Companion.USER_INFO
import com.umpa2020.tracer.network.BaseFB.Companion.USER_RAN_THESE_MAPS
import com.umpa2020.tracer.util.UserInfo
import java.util.*

class FBRacingRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 첫 번째 실행 되어야하는 함수
   * 현재 유저와 다른 레이서들의 기록을 비교하여
   * 최고 기록일 경우 bestTime을 1로 설정하고
   * 최고 기록이 아닐 경우 bestTime을 0으로 설정하여
   * ranking 에 등록하는 함수
   */
  fun createRankingData(
    result: Boolean,
    racerData: InfoData,
    racingFinishListener: RacingFinishListener,
    racerSpeeds: MutableList<Double>
  ) {
    // 타임스탬프
    val timestamp = Date().time

    if (result) {
      val rankingData = RankingData(
        racerData.makersNickname,
        UserInfo.nickname,
        racerData.time,
        1,
        racerSpeeds.max().toString(),
        racerSpeeds.average().toString()
      )
      // 랭킹 맵에서
      db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
        .whereEqualTo(BEST_TIME, 1)
        .get()
        .addOnSuccessListener {
          for (document in it) {
            if (document.get("challengerNickname") == UserInfo.nickname) {
              if (racerData.time!!.toLong() < document.get(CHALLENGER_TIME) as Long) {
                db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
                  .document(document.id).update(BEST_TIME, 0)
              } else {
                rankingData.bestTime = 0
              }
            }
          }
          db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
            .document(UserInfo.autoLoginKey + timestamp).set(rankingData)

          getRacingFinishRank(result, racerData, racingFinishListener)
        }
    } else {
      getRacingFinishRank(result, racerData, racingFinishListener)
    }
  }

  /**
   * 랭킹맵에서 올라와 있는 기록들을
   * 1. 동일한 유저의 기록은 최고 기록만 저장
   * 2. 성공했을 때에만 N등
   * 3. 실패했을 경우 실패
   */
  private fun getRacingFinishRank(
    result: Boolean,
    racerData: InfoData,
    racingFinishListener: RacingFinishListener
  ) {
    val arrRankingData: ArrayList<RankingData> = arrayListOf()
    var arg = 0

    db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
      .orderBy(CHALLENGER_TIME, Query.Direction.ASCENDING)
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

            arrRankingData.add(recycleRankingData)
            index++
          }
        }
        racingFinishListener.getRacingFinish(arrRankingData, arg)
      }
  }

  /**
   * 메이커 인포데이터를 가져오는 함수
   */
  fun getMakerData(racerData: InfoData, getMakerDataListener: GetMakerDataListener) {
    lateinit var makerData: InfoData

    db.collection(MAP_INFO).document(racerData.mapTitle!!)
      .get()
      .addOnSuccessListener { document ->
        makerData = document.toObject(InfoData::class.java)!!
        getMakerDataListener.makerData(makerData)
      }
  }

  /**
   * 다른 사람 인포데이터를 가져오는 함수
   */
  fun getOtherData(mapTitle: String, nickname: String, racingFinishListener: RacingFinishListener) {

    db.collection(RANKING_MAP).document(mapTitle)
      .collection(RANKING).whereEqualTo("challengerNickname", nickname)
      .whereEqualTo(BEST_TIME, 1)
      .get()
      .addOnSuccessListener {
        val rankingData = it.documents.last().toObject(RankingData::class.java)
        racingFinishListener.getOtherRacing(rankingData!!)
      }
  }

  /**
   * 유저 인포에 해당 유저가 이 맵을 뛰었다는
   * 히스토리를 더하는 함수
   */
  fun createUserInfoRacing(racerData: InfoData) {
    val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
    db.collection(USER_INFO).document(UserInfo.autoLoginKey).collection(USER_RAN_THESE_MAPS)
      .add(ranMapsData)
  }
}