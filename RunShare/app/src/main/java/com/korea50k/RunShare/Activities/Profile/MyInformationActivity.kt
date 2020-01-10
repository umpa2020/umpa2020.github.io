package com.korea50k.RunShare.Activities.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.korea50k.RunShare.R
import com.korea50k.RunShare.Util.SharedPreValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_information.*
import kotlinx.android.synthetic.main.fragment_rank.view.*

class MyInformationActivity : AppCompatActivity() {

    var count = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_information)

        my_information_email_textview.text = SharedPreValue.getEMAILData(this)
        my_information_nickname_textview.text = SharedPreValue.getNicknameData(this)
        my_information_age_textview.text = SharedPreValue.getAgeData(this)
        my_information_sex_textview.text = SharedPreValue.getGenderData(this)
        Log.d("info", "nickname" + SharedPreValue.getNicknameData(this))
        Log.d("info", "my_information_age_textview" + SharedPreValue.getAgeData(this))
        Log.d("info", "my_information_sex_textview" + SharedPreValue.getGenderData(this))


        val adjustBtn = findViewById<View>(com.korea50k.RunShare.R.id.my_information_adjust_button) as Button
        adjustBtn.setOnClickListener{
            if(count)
                count = false
            else
                count = true

            if(count == true){ // 각 조건에 따라 버튼 텍스트 변경
                my_information_adjust_button.text = "확인"
                //TODO 프로필 사진 바꾸는 부분 코드 추가
            }
            else{
                my_information_adjust_button.text = "수정"
            }
        }
    }
/*
    fun onClick(v: View) {
        when (v.id) {
            R.id.my_information_adjust_button -> {
                Toast.makeText(this, "프로필 사진만 변경 할 수 있습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

 */
}
