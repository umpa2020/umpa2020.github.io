package com.umpa2020.tracer.main.challenge

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.challenge_loaclpopup.*

class RegionChoicePopup(context: Context) : AlertDialog(context) {
  var seoulList = arrayOf(
    "전체", "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구",
    "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구",
    "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구")

  var inceonlist = arrayOf(
    "전체", "강화군", "계양구", "남동구", "동구", "미추홀구", "부평구","서구","연수구",
    "옹진군", "중구"
    )

  var geonggilist = arrayOf(
    "전체", "가평군", "고양시", "과천시", "광명시", "광주시", "구리시","군포시","김포시",
    "남양주시", "동두천시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시",
    "양주시", "양평군", "여주시", "연천군", "오산시", "용인시", "의왕시", "의정부시", "이천시",
    "파주시","평택시", "포천시", "하남시", "화성시"
  )

  var Daejeonlist = arrayOf(
  "전체", "대덕구", "동구", "서구", "유성구", "중구"
  )

  var Daegulist = arrayOf(
    "전체", "남구", "달서구", "달성군", "동구", "북구", "서구","수성구","중구"
  )

  var Busanlist = arrayOf(
    "전체", "강서구", "기장군", "금정구", "남구", "동구", "동래구","부산진구","북구",
    "중구", "사상구", "사하구", "서구", "수영구", "연제구", "영도구","해운대구"
  )

  var Gangwonslist = arrayOf(
    "전체", "강릉시", "고성군", "동해시", "삼척시", "속초시", "양구군","양양군","영월군",
    "원주시", "인제군", "정선군", "철원군", "춘천시", "태백시", "평창군","홍천군",
    "화천군", "횡성군"
  )

  var Gwangjulist = arrayOf(
    "전체", "광산구", "남구", "동구", "북구", "서구"
  )

  var Ulsanlist = arrayOf(
    "전체", "다운동", "반구1동", "반구2동", "병영1동", "병영2동",
    "복산1동", "복산2동", "북정동", "약사동", "우정동", "중앙동", "태화동", "학성동"
  )

  var Gyungnamlist = arrayOf(
    "전체", "거제시", "거창군", "고성군", "김해시", "남해군", "밀양시", "사천시", "산청군",
    "양산시", "의령군", "진주시", "창녕군", "창원시", "통영시", "하동군", "함안군",
    "함양군", "합천군"
  )

  var Gyungbuklist = arrayOf(
    "전체", "경산시", "경주시", "고령군", "구미시", "군위군", "김천시", "문경시", "봉화군",
    "상주시", "성주군", "안동시", "영덕군", "영양군", "영주시", "영천시", "예천군",
    "울릉군", "울진군", "의성군", "청도군", "청송군", "칠곡군", "포항시"
  )

  var Jeonnamlist = arrayOf(
    "전체", "강진군", "고흥군", "곡성군", "광양시", "구례군", "나주시", "담양군", "목포시",
    "무안군", "보성군", "순천시", "신안군", "여수시", "영광군", "영암군", "완도군",
    "장성군", "장흥군", "진도군", "함평군", "해남군", "화순군"
  )

  var Jeonbuklist = arrayOf(
    "전체", "고창군", "군산시", "김제시", "남원시", "무주군", "부안군", "순창군", "완주군",
    "익산시", "임실군", "장수군", "전주시", "정읍시", "진안군"
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.challenge_loaclpopup)
    window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


    challenge_local.layoutManager = GridLayoutManager(context, 2)

    Seoul.setOnClickListener {
      challenge_local.adapter = localAdapter(seoulList)
    }
    Incheon.setOnClickListener {
      challenge_local.adapter = localAdapter(inceonlist)
    }

    Gyeongi.setOnClickListener {
      challenge_local.adapter = localAdapter(geonggilist)
    }

    Daejeon.setOnClickListener {
      challenge_local.adapter = localAdapter(Daejeonlist)
    }

    Daegu.setOnClickListener {
      challenge_local.adapter = localAdapter(Daegulist)
    }

    Busan.setOnClickListener {
      challenge_local.adapter = localAdapter(Busanlist)
    }

    Gangwon.setOnClickListener {
      challenge_local.adapter = localAdapter(Gangwonslist)
    }

    Gwangju.setOnClickListener {
      challenge_local.adapter = localAdapter(Gwangjulist)
    }

    Ulsan.setOnClickListener {
      challenge_local.adapter = localAdapter(Ulsanlist)
    }

    Gyungnam.setOnClickListener {
      challenge_local.adapter = localAdapter(Gyungnamlist)
    }

    Gyungbuk.setOnClickListener {
      challenge_local.adapter = localAdapter(Gyungbuklist)
    }

    Jeonnam.setOnClickListener {
      challenge_local.adapter = localAdapter(Jeonnamlist)
    }

    Jeonbuk.setOnClickListener {
      challenge_local.adapter = localAdapter(Jeonbuklist)
    }
  }
}
