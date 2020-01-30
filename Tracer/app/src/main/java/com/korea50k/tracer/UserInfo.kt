package com.korea50k.tracer

import com.chibatching.kotpref.KotprefModel

/**
 *  Shared에 저장된 UserInfo
 */
object UserInfo : KotprefModel(){
    var autoLoginKey by stringPref() // 자동 로그인 유무 판단을 위해 firebase의 mAuth!!.currentUser 저장
    var email by stringPref() // 구글 이메일
    var nickname by stringPref() // 로그인 한 사람의 닉네임
}