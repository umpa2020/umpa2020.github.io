package com.umpa2020.tracer.extensions

import java.text.SimpleDateFormat
import java.util.*
import javax.xml.transform.SourceLocator

const val YEAR_MONTH_DAY_KR = "yyyy년 MM월 dd일"
const val MONTH_DAY_KR = "MM월 dd일"
const val YEAR_MONTH_DAY = "MMM dd, yyyy"
const val YEAR = "yyyy"
const val MONTH_DAY = "MMM dd"

const val mm_ss = "mm:ss"

const val Y_M_D = 0
const val M_D = 1
const val m_s = 2

/**
 * 날짜 시간 포맷 적용
 *
 * @param pattern yyyy-MM-dd'T'HH:mm:ssZ
 * @return 2020-03-21T18:34:10+0900
 */


fun Long.format(pattern: Int): String {
  val locale = Locale.getDefault()
  val type = when (pattern) {
    Y_M_D -> {
      when(locale){
        Locale.KOREA-> YEAR_MONTH_DAY_KR
        else-> YEAR_MONTH_DAY
      }
    }
    //올해면 월, 일 만
    M_D -> {
      val date = Date()
      //올해인지 검사
      if (SimpleDateFormat(YEAR, Locale.getDefault()).format(this)
        == SimpleDateFormat(YEAR, Locale.getDefault()).format(date.time)
      ) {
        when (locale) {
          Locale.KOREA -> MONTH_DAY_KR
          else -> MONTH_DAY
        }
      } else {
        when (locale) {
          Locale.KOREA -> YEAR_MONTH_DAY_KR
          else -> YEAR_MONTH_DAY
        }
      }
    }
    m_s -> mm_ss
    else -> YEAR_MONTH_DAY

  }
  return this.format(type)
}

fun Long.format(pattern: String): String {
  return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

/**
 * 날짜 문자열을 millisecond 로 변환
 *
 * @param pattern yyyy-MM-dd HH:mm:ss
 * @return 1584783920000
 */
fun String.toMillisecond(pattern: String): Long? {
  try {
    return SimpleDateFormat(pattern, Locale.getDefault()).parse(this).time
  } catch (ignore: Exception) {
  }
  return null
}

/**
 * 날짜 문자열을 다른 포맷으로 변경
 *
 * @param from yyyy-MM-dd HH:mm:ss
 * @param to   yyyy/MM/dd
 * @return 2018/01/10
 */
fun String.format(from: String, to: String): String? {
  return toMillisecond(from)?.format(to)
}
