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


  /**
   * 복합 색인 되면 이걸로
   */

  fun listChallengeData(fromDate: Long, toDate: Long, region: String, challengeDataListener: ChallengeDataListener) {
    val listChallengeData = mutableListOf<ChallengeData>()

    if (region == "전체") {
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

  /**
   * 복합 색인 안되가지고 이걸로 진행하겠음..
   *//*

  fun listChallengeData(
    fromDate: Long,
    toDate: Long,
    region: String,
    challengeDataListener: ChallengeDataListener
  ) {
    val listChallengeData = mutableListOf<ChallengeData>()


    db.collection("challenges")
      .whereGreaterThan("from", fromDate)
      .get()
      .addOnSuccessListener {
        it.documents.forEach { document ->
          val to = document.get("to") as Long
          Logg.d("to = $to")
          if (region != "전체") {

            val regionList = document.get("locale") as MutableList<String>
            if (to < toDate && regionList.contains(region)) {
              listChallengeData.add(document.toObject(ChallengeData::class.java)!!)
            }
          }
          else {

            if (to < toDate) {
              listChallengeData.add(document.toObject(ChallengeData::class.java)!!)
            }
          }
        }
        challengeDataListener.challengeDataList(listChallengeData)
      }
  }*/
}