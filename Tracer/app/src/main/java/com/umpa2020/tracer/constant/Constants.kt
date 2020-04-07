package com.umpa2020.tracer.constant

class Constants {
  companion object {
    const val NICKNAME_RULE = "^(?=.*\\W).{3,12}\$"
    //이메일 정규식
    const val EMAIL_RULE = "^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}\$"

    // 성별 제한
    const val GENDER_RULE = "^(?=.*[a-zA-Z]).{0,10}\$"

    // 나이 제한
    const val AGE_RULE = "^(?=.*\\d).{1,2}\$"

    //체크포인트에 도착 범위(단위 : 미터)
    const val ARRIVE_BOUNDARY = 10
    //경로이탈 범위(단위 : 미터)
    const val DEVIATION_DISTANCE = 20.0
    //경로이탈 카운트
    const val DEVIATION_COUNT = 30
    const val WPINTERVAL = 100  //way point interval

    //handler what
    const val INFOUPDATE = 1 //distance and speed update
    const val RACINGFINISH = 2 //Racing is finished
    const val DEVIATION = 3 //racer deviate the track
    const val MAPISREADY=4 //on map ready call back

    //최대 거리
    const val MAX_DISTANCE = 100000

    //seekerBar Max
    const val MAX_SEEKERBAR = 100

    //animation duration
    const val ANIMATION_DURATION_TIME = 500L
  }
}