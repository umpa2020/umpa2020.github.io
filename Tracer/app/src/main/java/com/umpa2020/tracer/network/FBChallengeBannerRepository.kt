package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.BannerData

/**
 * 배너 이미지 가져오는 Repository
 */

class FBChallengeBannerRepository : BaseFB() {

  /**
   * 바로 imageView 를 가져와서 등록한다.
   */

  fun listChallengeBannerImagePath(bannerDataListener: BannerDataListener) {
    val listBannerData = arrayListOf<BannerData>()
    db.collection(CHALLENGE_BANNERS).get()
      .addOnSuccessListener {
        it.documents.forEach { document ->
          listBannerData.add(document.toObject(BannerData::class.java)!!)
        }
        bannerDataListener.bannerDataList(listBannerData)
      }
  }
}