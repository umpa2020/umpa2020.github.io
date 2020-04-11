package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.LikeMapsData

/**
 * 내가 좋아요 누른 맵 목록 리스너
 */
interface LikedMapListener {
  /**
   * 내가 좋아요 누른 맵 목록
   */
  fun liked(likedMaps: List<LikeMapsData>)
}