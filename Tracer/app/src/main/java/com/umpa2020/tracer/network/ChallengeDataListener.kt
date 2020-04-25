package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.LikedMapData

/**
 * 챌린지 데이터 리스너
 */
interface ChallengeDataListener {
  /**
   * 챌린지 데이터 리스트 받아오는 리스너
   */
  fun challengeDataList(listChallengeData: MutableList<ChallengeData>){
  }


}