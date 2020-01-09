package com.korea50k.RunShare.Activities.Login

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import com.korea50k.RunShare.Join.SignUpActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.SharedPreValue
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    var WSY = "WSY"

    private var mEmailView: EditText? = null
    private var mPasswordView: EditText? = null

    private var email : String? = null
    private var password : String? = null
    private var loginChecked : Boolean? = null
    private var settings : SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),
                1
            )
        }

        Log.d(WSY,SharedPreValue.getAutoLogin(this).toString())
        mEmailView = findViewById(R.id.login_id) as EditText
        mPasswordView = findViewById(R.id.login_password) as EditText
    }

    fun onClick(v : View?){
        when(v!!.id){
            R.id.button_login -> {
                email = mEmailView?.getText().toString()
                password = mPasswordView?.getText().toString()
                Log.d(WSY,email + ", " + password)
                RetrofitClient.retrofitService.login(email!!,password!!).enqueue(object : retrofit2.Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
                            val result = response.body() as ResponseBody
                            val resultValue = result.string()

                            Log.i(WSY,"결과 : " + resultValue)

                            var userData = JSONObject(resultValue)

                            Log.i(WSY,userData.getString("Id") + ", " + userData.getString("Nickname"))
                            SharedPreValue.setEMAILData(this@LoginActivity,userData.getString("Id"))
                            SharedPreValue.setPWDData(this@LoginActivity,userData.getString("Password"))
                            SharedPreValue.setIDData(this@LoginActivity,userData.getString("Nickname"))
                            SharedPreValue.setAutoLogin(this@LoginActivity,true)

                            var nextIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(nextIntent)
                            finish()
                        }catch (e : Exception){
                            e.toString()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }
                })
            }
            R.id.button_signup -> {
                val intent = Intent(applicationContext, SignUpActivity::class.java)
                startActivityForResult(intent,101)
            }
//            R.id.button_google_login -> {
//
//            }
        }
    }

    // 회원가입 성공 시 자동 email, password입력
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==101 && resultCode == RESULT_OK){
            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            mEmailView?.setText(data?.getStringExtra("ID"))
            mPasswordView?.setText(data?.getStringExtra("PW"))
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}