package com.umpa2020.tracer.network

/**
 * 내가 좋아요 누른 맵 목록 리스너
 */
interface ProfileListener {
  /**
   * 내가 좋아요 누른 맵 목록
   */
  fun getProfile(distance: Double, time: Double)
}