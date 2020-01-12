package com.korea50k.RunShare.Join

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.Constants
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private var WSY = "WSY"

    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화 합니다.
    internal val viewDisposables = CompositeDisposable()

    private lateinit var inputDataField: Array<EditText>
    private lateinit var textInputLayoutArray: Array<TextInputLayout>
    private lateinit var inputInfoMessage: Array<String>
    private var isInputCorrectData: Array<Boolean> = arrayOf(false, false, false, false, false, false)

    // 카메라 requestCode
    private val PICK_FROM_ALBUM = 1

    private var bitmapImg : Bitmap? = null
    private var basicBitmapImg : Bitmap? = null
    private var email: String? = null
    private var password: String? = null
    private var passwordCheck: String? = null
    private var nickname: String? = null
    private var age: String? = null
    private var gender: String? = null

    private var isEmail = false
    private var isPassword = false
    private var isName = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sign_up)

        editEmail.requestFocus()
        init()

        editEmail.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v : View?, hasFocus: Boolean) {
                if (!hasFocus && isInputCorrectData[0]) {
                    idCheck(editEmail.text.toString())
                }
            }
        })
        editNickname.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v : View?, hasFocus: Boolean) {
                if (!hasFocus && isInputCorrectData[0]) {
                    nicknameCheck(editNickname.text.toString())
                }
            }
        })
    }

    /**
     * 카메라 접근 권한 and 앨범 접근
     */
    private fun goToAlbum() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            var length = permissions.size
            for(i in 0..length-1){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d(WSY,"권한 허용 : " + permissions[i])
                    goToAlbum()
                }
            }
        }
    }

    private fun checkSelfPermission(){
        var temp = ""

        // 파일 읽기 권한 확인
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
        }

        Log.d(WSY,temp)
        if(TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" ").toTypedArray(),1)
        }else {
            // 모든 허용 상태
            //toast("권한을 모두 허용")
            goToAlbum()
        }
    }



    private fun init() {
        inputDataField = arrayOf(editEmail, editPassword, editPasswordCheck, editNickname, editAge, editGender)
        textInputLayoutArray =
            arrayOf(emailTextInput, pwTextInput, pwCkTextInput,  nicknameTextInput, ageTextInput, genderTextInput)
        inputInfoMessage = arrayOf(
            getString(R.string.error_discorrent_email), getString(
                R.string.txtInputInfoPWD
            ), getString(R.string.txtInputInfoPWCHECK),getString(R.string.txtInputInfoNick), getString(R.string.txtInputInfoAge), getString(
                R.string.txtInputInfoGender
            )
        )

        typingListener()
    }

    /**
     * 각 필드별 회원가입 조건이 맞는지 비동기 체크
     */
    private fun typingListener() {
        // ID
        val disposableEmail = RxTextView.textChanges(inputDataField[0])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.EMAIL_RULS, t) }
            .subscribe({ it ->
                //  isCheckID = false
                Log.i(WSY, it.toString())
                reactiveInputTextViewData(0, it)
            }) {
                //Error Block
                //settingEmptyInputUI(0)
            }

        // Password
        val disposablePwd = RxTextView.textChanges(inputDataField[1])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.PASSWORD_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(1, it)
            }) {
                //Error Block
            }
        // Password
        val disposablePwdCheck = RxTextView.textChanges(inputDataField[2])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.PASSWORD_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(2, it)
            }) {
                //Error Block
            }

        //Nickname
        val disposableNick = RxTextView.textChanges(inputDataField[3])
            .map { t -> t.isEmpty() || !Pattern.matches(Constants.NICKNAME_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(3, it)
            }) {
                //Error Block
            }

        val disposableAge = RxTextView.textChanges(inputDataField[4])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.AGE_RULS, t)}
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(4, it)
            }) {
                //Error Block
            }

        val disposableGender = RxTextView.textChanges(inputDataField[5])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.GENDER_RULS, t)}
            .subscribe({ it ->
                //inputDataField[2].setText("")
                Log.d(WSY,"성별 : " + it.toString())
                reactiveInputTextViewData(5, it)
            }) {
                //Error Block
            }
        viewDisposables.addAll(
            disposableEmail,
            disposablePwd,
            disposableNick,
            disposableAge,
            disposableGender
        )
    }

    private fun settingEmptyInputUI(indexPath: Int) {
        isInputCorrectData[indexPath] = false
        textInputLayoutArray[indexPath].isErrorEnabled = false
    }

    /**
     * ReActive 로 입력 들어오는 데이터에 대한 결과를 UI 로 표시합니다
     */
    private fun reactiveInputTextViewData(indexPath: Int, it: Boolean) {
        if (!inputDataField[indexPath].text.toString().isEmpty()) {
            isInputCorrectData[indexPath] = it
        } else {
            isInputCorrectData[indexPath] = false
        }

        textInputLayoutArray[indexPath].error = inputInfoMessage[indexPath]
        textInputLayoutArray[indexPath].isErrorEnabled = !it

        reactiveCheckCorrectData()
    }

    var options : BitmapFactory.Options? = null
    var resized : Bitmap? = null
    // intent 결과 받기
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        // 성별
        if (requestCode == 102 && resultCode == RESULT_OK) {
            editGender.setText(intentData!!.getStringExtra("Gender"))
        }

        // 앨범
        if (requestCode == PICK_FROM_ALBUM) {
//
             if(resultCode == RESULT_OK)
            {
                try{
                    var inputStream =
                        intentData!!.data?.let { getContentResolver().openInputStream(it) }

                    // 프로필 사진을 비트맵으로 변환
                    options = BitmapFactory.Options()
                    options!!.inSampleSize = 2
                    bitmapImg = BitmapFactory.decodeStream(inputStream,null, options)
                    inputStream!!.close()

                    resized = Bitmap.createScaledBitmap(bitmapImg!!,bitmapImg!!.width, bitmapImg!!.height, true)


                    profileImage.setImageBitmap(bitmapImg)
                    profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    //TODO:서버로 데이터 전송
                }catch(e : java.lang.Exception)
                {

                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                //사진 선택 취소
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 올바른 회원정보를 입력 받았는지 체크
     */
    var isSuccess = false

    private fun reactiveCheckCorrectData() {
        for (check in isInputCorrectData) {
            if (!check) {
                isSuccess = false
                Log.d(WSY, "입력 상황 : " + check.toString())
                return
            }
        }
        isSuccess = true
    }

    // 바탕 클릭 시 키패드 숨기기
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }

    // 서버로의 회원가입 진행
    private fun signUp(
        bitmapImg : Bitmap,
        email: String,
        password: String,
        nickname: String,
        age: String,
        gender: String
    ) {
        var byteArrayOutputStream = ByteArrayOutputStream()
        resized!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        var byteArray = byteArrayOutputStream.toByteArray()
        var base64OfBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // 이미지
        Log.d(WSY, email + ", " + password + ", " + nickname + ", " + age + ", " + gender)
        RetrofitClient.retrofitService.signUp(base64OfBitmap,email, password, nickname, age, gender)
            .enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    // 응답오는게 꼭 정상적인 결과가 아닐 수도 있기 때문에 20x, 40x로 구분.
                    // 웹에서 결과 값? 끌어오기
                    try {
//                        Log.d(WSY, response.body()?.string())
                        val intent = Intent()
                        intent.putExtra("ID", email)
                        intent.putExtra("PW", password)
                        setResult(RESULT_OK, intent)
                        finish()
                    } catch (e: Exception) {

                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) { // 진짜 서버 작업 중 실패 한것. ex) 서버 오류, DB 쿼리 문제...
                    Toast.makeText(this@SignUpActivity, "서버 작업 실패", Toast.LENGTH_SHORT).show()
                    t.printStackTrace()
                }
            })
    }

    // 서버 DB랑의 ID 체크
    private fun idCheck(email: String) {
        RetrofitClient.retrofitService.get_IdCheck(email)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        if (response.isSuccessful) {
                            //the response-body is already parseable to your ResponseBody object
                            val responseBody = response.body() as ResponseBody
                            //you can do whatever with the response body now...
                            val responseBodyString = responseBody.string() // 웹에서 가져온 값
                            Log.d(WSY, responseBodyString.length.toString())
                            val check = responseBodyString.substring(0, 1) // 쓰레기 값이랑 분리

                            Log.d(WSY, check)
                            if (check.equals("1")) { // 사용가능한 아이디
                                //  emailCheckTextView?.setText("멋진 아이디네요!")
                                toast("사용 가능한 이메일입니다.")
                                isEmail = true
                            } else { // 사용 불가 아이디
                                //  emailCheckTextView?.setText("이미 사용 중인 아이디입니다")
                                toast("이미 사용 중인 이메일입니다.")
                                isEmail = false
                                isInputCorrectData[0] = false
                                Log.d(WSY, "zzzzz" + isEmail)
                            }
                        } else {
                            Log.d(WSY, response.errorBody().toString())
                        }
                    } catch (e: Exception) {
                        Log.i(WSY, e.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "Id Check 서버 작업 실패", Toast.LENGTH_SHORT)
                        .show()
                    t.printStackTrace()
                }
            })
    }

    // 서버 DB랑의 ID 체크
    private fun nicknameCheck(nickname: String) {
        RetrofitClient.retrofitService.get_NicknameCheck(nickname)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        if (response.isSuccessful) {
                            //the response-body is already parseable to your ResponseBody object
                            val responseBody = response.body() as ResponseBody
                            //you can do whatever with the response body now...
                            val responseBodyString = responseBody.string() // 웹에서 가져온 값
                            Log.d(WSY, responseBodyString.length.toString())
                            val check = responseBodyString.substring(0, 1) // 쓰레기 값이랑 분리

                            Log.d(WSY, check)
                            if (check.equals("1")) { // 사용가능한 아이디
                                //  emailCheckTextView?.setText("멋진 아이디네요!")
                                toast("사용 가능한 닉네임입니다.")
                                isEmail = true
                            } else { // 사용 불가 아이디
                                //  emailCheckTextView?.setText("이미 사용 중인 아이디입니다")
                                toast("이미 사용 중인 닉네임입니다.")
                                isEmail = false
                                isInputCorrectData[0] = false
                                Log.d(WSY, "zzzzz" + isEmail)
                            }
                        } else {
                            Log.d(WSY, response.errorBody().toString())
                        }
                    } catch (e: Exception) {
                        Log.i(WSY, e.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "Id Check 서버 작업 실패", Toast.LENGTH_SHORT)
                        .show()
                    t.printStackTrace()
                }
            })
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.backImageBtn->{
                finish()
            }
            R.id.editAge -> {
//                var intent = Intent(this, AgeSelectActivity::class.java)
//                startActivityForResult(intent,101)

//                var builder = AlertDialog.Builder(this)
//                builder.setTitle("나이를 선택하세요.").setMessage("반갑습니다.")
//                var alertDialog = builder.create()
//                alertDialog.show()

            }
            R.id.editGender -> {
                var intent = Intent(this, GenderSelectActivity::class.java)
                startActivityForResult(intent, 102)

            }
            R.id.profileImage -> {
               // tedPermission()
                checkSelfPermission()
            }
            R.id.sign_up_button -> {

                email = editEmail.text.toString()
                password = editPassword.text.toString()
                passwordCheck = editPasswordCheck.text.toString()
                nickname = editNickname.text.toString()
                age = editAge.text.toString()
                gender = editGender.text.toString()

                Log.d(WSY,"" + isInputCorrectData[0] + ", " + isInputCorrectData[1] + ", " + isInputCorrectData[2] + ", " + isInputCorrectData[3] + ", " + isInputCorrectData[4]         )
                Log.d(WSY, email + ", " + password + ", "  + passwordCheck +", " +  nickname + ", " + age + ", " + gender)

                // 비밀번호 확인
                if(!password.equals(passwordCheck)) {
                    editPasswordCheck.text = null
                    textInputLayoutArray[2].error = inputInfoMessage[2]
                    textInputLayoutArray[2].isErrorEnabled = true
                }
                // 비밀 번호 확인이 되면...가입!
                else {
                    if (isInputCorrectData[0] && isInputCorrectData[1] && isInputCorrectData[3] && age!!.isNotEmpty() && gender!!.isNotEmpty()) {
                        // AsyncTask로 로딩(시간이 걸리는 것을) 보여주기

                        if(bitmapImg == null) {
                            // 기본 프로필로 가입하기
                            basicBitmapImg = BitmapFactory.decodeResource(resources, R.drawable.basic_profile)
                            signUp(basicBitmapImg!!, email!!, password!!, nickname!!, age!!, gender!!)
                        }
                        else {
                            // 등록한 프로필 사진으로 가입하기
                            signUp(bitmapImg!!, email!!, password!!, nickname!!, age!!, gender!!)
                        }
                    }
                    // 정보가 하나라도 입력 안되면 error 메시지 출력
                    else{
                        for(a in 0..5) {
                            // 에러 메시지 중에서도 정보가 입력되것 제외하고 출력.
                            if(!isInputCorrectData[a])
                                reactiveInputTextViewData(a, false)
                        }
                    }
                }

            }
        }
    }
}
