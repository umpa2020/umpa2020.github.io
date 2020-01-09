package com.korea50k.RunShare.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.korea50k.RunShare.Activities.Login.LoginActivity
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.SharedPreValue
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    val WSY = "WSY"

    val userID : String? by lazy {
        SharedPreValue.getIDData(this)
    }
    val userEmail : String? by lazy {
        SharedPreValue.getEMAILData(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ // 앞의 과정이 약간의 시간이 필요하거나 한 경우 바로 어떤 명령을 실행하지 않고 잠시 딜레이를 갖고 실행
            Log.i(WSY,"프리퍼런스에 저장된 자동 로그인 유무 :" + SharedPreValue.getAutoLogin(this).toString())
            if(SharedPreValue.getAutoLogin(this)){ // 자동 로그인 체크되어있었으면
                RetrofitClient.retrofitService.login(
                    SharedPreValue.getEMAILData(this)!!,
                    SharedPreValue.getPWDData(this)!!).enqueue(object : retrofit2.Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Log.i(WSY,"자동 로그인 성공!")
                        var nextIntent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(nextIntent)
                        finish()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }
                })

                // 이메일이랑 닉네임으로 서버DB랑 비교 후 메인 화면으로?

//                    return@postDelayed
////                }
            }else {

//            startActivity(LoginActivity.newIntent(this))
                var nextIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(nextIntent)
                finish()
            }
        }, 800)
    }
}
