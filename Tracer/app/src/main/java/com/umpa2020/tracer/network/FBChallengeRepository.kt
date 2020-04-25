package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.LikedMapData
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
  fun settingBanner(challengeData: ChallengeData) {

  }
}