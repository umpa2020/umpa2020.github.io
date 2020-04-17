package com.umpa2020.tracer.main.profile.myrecord

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RecordData
import kotlinx.android.synthetic.main.activity_profile_record.*


class ProfileRecordActivity : AppCompatActivity() {

  //lateinit var getRedordDatas: ArrayList<RecordData>


  //TODO recorddatgara은 test용이기 떄문에 바꾸기
  val recorddatgara = arrayListOf<RecordData>(
    RecordData("asdf", "박문수님이 똥을 싸셨습니다."),
    RecordData("asdfasdf", "김정빈은 쉬를 싸러 갔네요")
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_record)

    //adpater 추가
    profileRecyclerRecord.adapter = ProfileRecyclerViewAdapterRecord(recorddatgara)
    profileRecyclerRecord.layoutManager = LinearLayoutManager(App.instance)

    Toast.makeText(this, "adapter 추가 이후", Toast.LENGTH_LONG).show()
  }

}