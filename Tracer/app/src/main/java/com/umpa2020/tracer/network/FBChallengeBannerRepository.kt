package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.BannerData
import kotlinx.coroutines.tasks.await

/**
 * 배너 이미지 가져오는 Repository
 */

class FBChallengeBannerRepository : BaseFB() {

  /**
   * 바로 imageView 를 가져와서 등록한다.
   */

  suspend fun listChallengeBannerImagePath(): MutableList<BannerData>? {
    return db.collection(CHALLENGE_BANNERS).get()
      .await()
      .documents.map { it.toObject(BannerData::class.java)!! }.toMutableList()
  }
}