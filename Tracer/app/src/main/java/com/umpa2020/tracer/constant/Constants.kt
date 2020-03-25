package com.umpa2020.tracer.constant

class Constants {
    companion object {
        val NICKNAME_RULS = "^(?=.*\\W).{3,12}\$"
        //이메일 정규식
        val EMAIL_RULS = "^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}\$"

        // 성별 제한
        val GENDER_RULS = "^(?=.*[a-zA-Z]).{0,10}\$"

        // 나이 제한
        val AGE_RULS = "^(?=.*\\d).{1,2}\$"

        //체크포인트에 도착 범위(단위 : 미터)
        val NEAR_DISTANCE = 10
        //경로이탈 범위(단위 : 미터)
        val DEVIATION_DISTANCE=20.0
        //경로이탈 카운트
        val DEVIATION_COUNT=30
        val WPINTERVAL = 100  //way point interval

        //handler what
        val INFOUPDATE = 1 //distance and speed update
        val RACINGFINISH = 2 //Racing is finished
    }
}