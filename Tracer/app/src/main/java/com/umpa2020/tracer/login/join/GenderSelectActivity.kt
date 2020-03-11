package com.umpa2020.tracer.login.join

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.activity_gender_select.*
import kotlinx.android.synthetic.main.signup_toolbar.view.*

class GenderSelectActivity : AppCompatActivity() {
    val WSY = "WSY"

    var isMan = false
    var isWoman = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_gender_select)

        val titleText = app_toolbar.titleText
        titleText.setText("성별을 선택하세요.")
    }

    fun onClick(v : View){
        when (v.id) {
            R.id.backImageBtn -> {
                finish()
            }
            R.id.nextButton ->{
                try {
                    val intent = Intent()
                    if(isMan)
                        intent.putExtra("Gender","Man")
                    else
                        intent.putExtra("Gender","Woman")
                    setResult(RESULT_OK, intent)
                    finish()
                } catch (e: Exception) {

                }
            }
            R.id.man -> {

                if (manCheck.visibility == View.INVISIBLE && manUnderline.visibility == View.INVISIBLE) {
                    manCheck.visibility = View.VISIBLE
                    manUnderline.visibility = View.VISIBLE

                    womanCheck.visibility = View.INVISIBLE
                    womanUnderline.visibility = View.INVISIBLE
                    isWoman = false
                    isMan = true
                } else {
                    manCheck.visibility = View.INVISIBLE
                    manUnderline.visibility = View.INVISIBLE

                    womanCheck.visibility = View.VISIBLE
                    womanUnderline.visibility = View.VISIBLE

                    isMan = false
                    isWoman = true
                }
                Log.d(WSY,"남자 : " + isMan.toString())
                Log.d(WSY,"여자 : " + isWoman.toString())
            }
            R.id.woman -> {
                if (womanCheck.visibility == View.INVISIBLE && womanUnderline.visibility == View.INVISIBLE) {
                    womanCheck.visibility = View.VISIBLE
                    womanUnderline.visibility = View.VISIBLE

                    manCheck.visibility = View.INVISIBLE
                    manUnderline.visibility = View.INVISIBLE

                    isMan = false
                    isWoman = true
                } else {
                    womanCheck.visibility = View.INVISIBLE
                    womanUnderline.visibility = View.INVISIBLE

                    manCheck.visibility = View.VISIBLE
                    manUnderline.visibility = View.VISIBLE

                    isWoman = false
                    isMan = true
                }
                Log.d(WSY,"여자 : " + isWoman.toString())
                Log.d(WSY,"남자 : " + isMan.toString())
            }

        }
        if(isMan || isWoman) {
            nextButton.isEnabled = true
        }
    }
}
