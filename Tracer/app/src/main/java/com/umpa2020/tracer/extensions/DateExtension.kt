package com.umpa2020.tracer.extensions

import java.text.SimpleDateFormat
import java.util.*

const val ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ"
const val YEAR_MONTH_DAY_KR = "yyyy년 MM월 dd일"
const val MONTH_DAY_KR = "MM월 dd일"
const val YEAR_MONTH_DAY = "MMM dd, yyyy"
const val YEAR = "yyyy"
const val MONTH_DAY = "MMM dd"

const val YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss"
const val MM_SS = "mm:ss"

enum class ENMonths {
  Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
}

/**
 * 날짜 시간 포맷 적용
 *
 * @param pattern yyyy-MM-dd'T'HH:mm:ssZ
 * @return 2020-03-21T18:34:10+0900
 */
fun Long.format(pattern: String): String {
  return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.format(locale: Locale): String {
  val date = Date()
  if (SimpleDateFormat(YEAR, Locale.getDefault()).format(this) == SimpleDateFormat(YEAR, Locale.getDefault()).format(date.time)) {
    return when (locale) {
      Locale.KOREA -> SimpleDateFormat(MONTH_DAY_KR, locale).format(this)
      else -> {
        SimpleDateFormat(MONTH_DAY, locale).format(this)
      }
    }
  }
  else {
    return when (locale) {
      Locale.KOREA -> SimpleDateFormat(YEAR_MONTH_DAY_KR, locale).format(this)
      else -> {
        SimpleDateFormat(YEAR_MONTH_DAY, locale).format(this)
      }
    }
  }
}

/**
 * 현재 시간 표시
 *
 * @return 2020-03-21T18:34:10+0900
 */
fun Long.now(): String {
  return format(ISO8601)
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
 * 날짜 문자열을 millisecond 로 변환
 * 어떤 포맷으로 올지 모른다면 사용하는 모든 포맷을 검사한다.
 *
 * 배열에는 긴 것부터 넣어야 한다.
 *
 * @param pattern yyyy-MM-dd HH:mm:ss
 * @return 1584783920000
 */
// fun String.toMillisecond(pattern: String): Long? {
//  arrayOf(pattern)
//  .plus(ISO8601)
//  .plus(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
//  .plus(YEAR_MONTH_DAY).forEach {
//    try {
//      return SimpleDateFormat(it, Locale.getDefault()).parse(this).time
//    } catch (ignore: Exception) {
//    }
//  }
//  return null
// }

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
