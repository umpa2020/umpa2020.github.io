package com.korea50k.RunShare.Activities.Profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.korea50k.RunShare.Splash.SplashActivity
import com.korea50k.RunShare.Util.SharedPreValue


class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.korea50k.RunShare.R.layout.activity_setting)
    }

    fun onClick(v : View){
        when(v.id){
            com.korea50k.RunShare.R.id.logout_button->{
                var builder =  AlertDialog.Builder(this)
                builder.setTitle("안내").setMessage("로그아웃 하시겠습니까?")

                builder.setPositiveButton("확인", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        SharedPreValue.AllDataRemove(applicationContext)
                        restartApp()
                    }
                })
                builder.setNegativeButton("취소", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                })

                Log.d("WSY","Shared 저장 이메일 : " + SharedPreValue.getEMAILData(this))
                Log.d("WSY","Shared 저장 비번 : " + SharedPreValue.getPWDData(this))
                Log.d("WSY","Shared 저장 닉네임 : " + SharedPreValue.getNicknameData(this))
                Log.d("WSY","Shared 저장 나이 : " + SharedPreValue.getAgeData(this))
                Log.d("WSY","Shared 저장 성별 : " + SharedPreValue.getGenderData(this))
                var alertDialog = builder.create()
                alertDialog.show()
            }
        }
    }

    fun restartApp() {
        finishAffinity()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        System.exit(0)
    }
}
