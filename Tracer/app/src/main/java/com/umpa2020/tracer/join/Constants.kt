package com.umpa2020.tracer.join

class Constants {
    companion object {
        val NICKNAME_RULS = "^(?=.*\\W).{3,12}\$"
        //이메일 정규식
        val EMAIL_RULS = "^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}\$"

        // 성별 제한
        val GENDER_RULS = "^(?=.*[a-zA-Z]).{0,10}\$"

        // 나이 제한
        val AGE_RULS = "^(?=.*\\d).{1,2}\$"
    }
}