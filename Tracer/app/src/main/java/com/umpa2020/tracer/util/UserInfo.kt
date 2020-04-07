package com.umpa2020.tracer.util

import com.chibatching.kotpref.KotprefModel
import com.google.android.gms.maps.model.LatLng

/**
 *  Shared에 저장된 UserInfo
 */
object UserInfo : KotprefModel() {
  var autoLoginKey by stringPref() // 자동 로그인 유무 판단을 위해 firebase의 mAuth!!.currentUser 저장
  var email by stringPref() // 구글 이메일
  var nickname by stringPref() // 로그인 한 사람의 닉네임
  var age by stringPref()
  var gender by stringPref()
  var permission = 0 // 처음 권한을 받은 후에 서비스와 위치 값 갱신을 하기 위해서 미리 저장
  var rankingLatLng: LatLng? = null
}