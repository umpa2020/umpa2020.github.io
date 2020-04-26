package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo

/**
 *
 */

class FBChallengeRepository : BaseFB() {

  /**
   *
   */
  fun createChallengeData(challengeData: ChallengeData) {
    db.collection("challenges").add(challengeData)
  }


  /**
   *
   */
  fun createBanner(challengeData: ChallengeData) {

  }

  fun getChallengeData(challengeId: String, challengeDataListener: ChallengeDataListener) {
    db.collection("challenges")
      .whereEqualTo("id", challengeId)
      .get()
      .addOnSuccessListener {
        challengeDataListener.challengeData(it.documents.last().toObject(ChallengeData::class.java)!!)
      }
  }


  /**
   * 복합 색인 되면 이걸로
   */

  fun listChallengeData(fromDate: Long, toDate: Long, region: String, challengeDataListener: ChallengeDataListener) {
    val listChallengeData = mutableListOf<ChallengeData>()
    Logg.d("search $fromDate ~ $toDate $region")
    if (region == "전국") {
      db.collection("challenges")
        .whereGreaterThan("date", fromDate)
        .whereLessThan("date", toDate)
        .get()
        .addOnSuccessListener {
          it.documents.forEach { document ->
            listChallengeData.add(document.toObject(ChallengeData::class.java)!!)
          }

          challengeDataListener.challengeDataList(listChallengeData)
        }
    }
    else {
      db.collection("challenges")
        .whereGreaterThan("date", fromDate)
        .whereLessThan("date", toDate)
        .whereArrayContains("locale", region)
        .get()
        .addOnSuccessListener {
          it.documents.forEach { document ->
            listChallengeData.add(document.toObject(ChallengeData::class.java)!!)
          }

          challengeDataListener.challengeDataList(listChallengeData)
        }
    }
  }

}