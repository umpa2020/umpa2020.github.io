package com.umpa2020.tracer.util

class PrettyDistance {

  // 미터 단위, 키로 미터 단위를 정리해주는 클래스
  // 사용법 : PrettyDistance().convertPretty($distance)
  fun convertPretty(distance: Double): String {
    return if (distance < 1000) {
      String.format("%.0f", distance) + " m"
    } else {
      String.format("%.2f", distance / 1000) + " km"
    }
  }
}