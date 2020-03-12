package com.umpa2020.tracer.login.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.login.LoginActivity
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.system.exitProcess

class SplashActivity : AppCompatActivity() {
    val WSY = "WSY"
    private var mAuth: FirebaseAuth? = null

    // firebase DB
    private var mFirestoreDB: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /**
         *  Firestore 초기화
         */

        mFirestoreDB = FirebaseFirestore.getInstance()

        ObjectAnimator.ofFloat(redMovingView, "translationX", 2000f).apply {
            duration = 1500
            start()
        }


        Log.i(WSY, "프리퍼런스에 저장된 자동 로그인 유무 : ${UserInfo.autoLoginKey}")
        Handler().postDelayed({
            // 앞의 과정이 약간의 시간이 필요하거나 한 경우 바로 어떤 명령을 실행하지 않고 잠시 딜레이를 갖고 실행
            Log.d(WSY, UserInfo.autoLoginKey)
            /**
             *  // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
             */
            if (!UserInfo.autoLoginKey.equals("")) { // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
                // main으로
                var nextIntent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(nextIntent)
                finish()

                //  로그인 고유 값이 있는데 로그아웃으로 인한것이면 Db에서 개인 정보가 있는지 검사가 필요??
                // 검사해서 데이터가 있으면...음....ㅠㅠ
            } else { // shared에 로그인 ID 고유값이 없으면 초기 가입자 or (로그아웃 or 앱 삭제 후 재 로그인)
//                mFirestoreDB!!.collection("userinfo").
                var nextIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(nextIntent)
                finish()
            }
        }, 800)
    }

    override fun onPause() {
        super.onPause()
        Log.d(WSY, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(WSY, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(WSY, "onDestroy()")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(WSY, "뒤로 감~~")
        finishAffinity() //  해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
        System.runFinalization() // 간단히 말해 현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어
        exitProcess(0) // 현재 액티비티를 종료시킨다.
    }
}
