package com.korea50k.RunShare.Util

class Constants {
    companion object {
//        val LOG_TEST = "LOG_LOGIN"
//        val INTENT_DATA = "com.kcs.weektest001.common.intent_data"

        //패스워드 정규식
        // 대문자,소문자, 숫자 또는 특수문자
//    private val PASSWORD_RULS = "^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{6,20}\$"

        // 대문자, 소문자 숫자, 특수문자 최소 8자 - 최대 20자
        val PASSWORD_RULS = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}\$"
        // 대문자, 소문자 숫자 최소 2자 - 최대 12자
//        val NICKNAME_RULS = "^(?=.*[a-zA-Z])(?=.*\\d).{3,12}\$"
        val NICKNAME_RULS = "^(?=.*\\W).{3,12}\$"
        //이메일 정규식
        val EMAIL_RULS = "^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}\$"

        val USER_TABLE_ID = "id"
    }
}