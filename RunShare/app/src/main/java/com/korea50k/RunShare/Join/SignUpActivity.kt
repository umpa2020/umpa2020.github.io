package com.korea50k.RunShare.Join

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
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
import java.io.File
import java.util.ArrayList
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private var WSY = "WSY"

    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화 합니다.
    internal val viewDisposables = CompositeDisposable()

    private lateinit var inputDataField: Array<EditText>
    private lateinit var textInputLayoutArray: Array<TextInputLayout>
    private lateinit var inputInfoMessage: Array<String>
    private var isInputCorrectData: Array<Boolean> = arrayOf(false, false, false, false, false)

    // 카메라 requestCode
    private val PICK_FROM_ALBUM = 1
    private var pofileImageFile: File? = null


    private var email: String? = null
    private var password: String? = null
    private var nickname: String? = null
    private var age: String? = null
    private var gender: String? = null

    private var isEmail = false
    private var isPassword = false
    private var isName = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()

        editEmail.setOnFocusChangeListener(object : View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus : Boolean) {
                if(!hasFocus && isInputCorrectData[0]) {
                    idCheck(editEmail.text.toString())
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
    private fun  tedPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Toast.makeText(this@SignUpActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionGranted() {
                Toast.makeText(this@SignUpActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                goToAlbum()
            }



        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(getResources().getString(R.string.permission_2))
            .setDeniedMessage(getResources().getString(R.string.permission_1))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check();
    }

    private fun setImage() {
        var imageView = profileImage
        var options = BitmapFactory.Options()
        var originalBm = BitmapFactory.decodeFile(pofileImageFile?.absolutePath, options)

        imageView.setImageBitmap(originalBm)
    }

    private fun init() {
        inputDataField = arrayOf(editEmail, editPassword, editNickname, editAge, editGender)
        textInputLayoutArray = arrayOf(emailTextInput, pwTextInput, nicknameTextInput, ageTextInput, genderTextInput)
        inputInfoMessage = arrayOf(getString(R.string.error_discorrent_email), getString(
            R.string.txtInputInfoPWD
        ), getString(R.string.txtInputInfoNick), getString(R.string.txtInputInfoAge), getString(
            R.string.txtInputInfoGender
        ))

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
                Log.i(WSY,it.toString())
                reactiveInputTextViewData(0, it)
            }){
                //Error Block
                //settingEmptyInputUI(0)
            }

        // Password
        val disposablePwd = RxTextView.textChanges(inputDataField[1])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.PASSWORD_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(1, it)
            }){
                //Error Block
            }

        //Nickname
        val disposableNick = RxTextView.textChanges(inputDataField[2])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.NICKNAME_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(2, it)
            }){
                //Error Block
            }

        val disposableAge  = RxTextView.textChanges(inputDataField[3])
            .map { t -> t.isEmpty() }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(3, it)
            }){
                //Error Block
            }

        val disposableGender = RxTextView.textChanges(inputDataField[4])
            .map { t -> t.isEmpty()}
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(4, it)
            }){
                //Error Block
            }
        viewDisposables.addAll(disposableEmail, disposablePwd, disposableNick, disposableAge, disposableGender)
    }
    private fun settingEmptyInputUI(indexPath: Int){
        isInputCorrectData[indexPath] = false
        textInputLayoutArray[indexPath].isErrorEnabled = false
    }

    /**
     * ReActive 로 입력 들어오는 데이터에 대한 결과를 UI 로 표시합니다
     */
    private fun reactiveInputTextViewData(indexPath: Int, it: Boolean) {
        if(!inputDataField[indexPath].text.toString().isEmpty()){
            isInputCorrectData[indexPath] = it
        }else{
            isInputCorrectData[indexPath] = false
        }

        textInputLayoutArray[indexPath].error = inputInfoMessage[indexPath]
        textInputLayoutArray[indexPath].isErrorEnabled = !it

        reactiveCheckCorrectData()
    }


    // intent 결과 받기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 성별
        if(requestCode==102 && resultCode == RESULT_OK){
            editGender.setText(data!!.getStringExtra("Gender"))
        }

        // 앨범
        if (requestCode == PICK_FROM_ALBUM){
            val photoUri = data?.getData()

            var cursor: Cursor? = null

            try {

                /*
             *  Uri 스키마를
             *  content:/// 에서 file:/// 로  변경한다.
             */
                val proj = arrayOf(MediaStore.Images.Media.DATA)

                assert(photoUri != null)
                cursor = contentResolver.query(photoUri!!, proj, null, null, null)

                assert(cursor != null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                cursor!!.moveToFirst()

                pofileImageFile = File(cursor!!.getString(column_index))

            } finally {
                if (cursor != null) {
                    cursor!!.close()
                }
            }

            setImage()
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
                Log.d(WSY,"입력 상황 : " + check.toString())
                return
            }
        }
        isSuccess =true
    }


    // 서버로의 회원가입 진행
    private fun signUp(email: String, password: String, nickname: String, age : String, gender : String) {
        Log.d(WSY, email + ", " + password + ", " + nickname + ", " + age + ", " + gender)
        RetrofitClient.retrofitService.signUp(email, password, nickname, age, gender).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 응답오는게 꼭 정상적인 결과가 아닐 수도 있기 때문에 20x, 40x로 구분.
                // 웹에서 결과 값? 끌어오기
                try {
                    Log.d(WSY, response.body()?.string())
                    val intent = Intent()
                    intent.putExtra("ID", email)
                    intent.putExtra("PW", password)
                    setResult(RESULT_OK, intent)
                    finish()
                } catch (e: Exception) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { // 진짜 서버 작업 중 실패 한것. ex) 서버 오류, DB 쿼리 문제...
                Toast.makeText(this@SignUpActivity, "서버 작업 실패", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }

    // 서버 DB랑의 ID 체크
    private fun idCheck(email: String) {
        RetrofitClient.retrofitService.get_IdCheck(email)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
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

    fun onClick( v : View){
        when(v.id){
            R.id.editAge ->{
//                var intent = Intent(this, AgeSelectActivity::class.java)
//                startActivityForResult(intent,101)

//                var builder = AlertDialog.Builder(this)
//                builder.setTitle("나이를 선택하세요.").setMessage("반갑습니다.")
//                var alertDialog = builder.create()
//                alertDialog.show()

            }
            R.id.editGender ->{
                var intent = Intent(this, GenderSelectActivity::class.java)
                startActivityForResult(intent,102)

            }
            R.id.profileImage ->{
                tedPermission()

            }
            R.id.sign_up_button_signup ->{
                // if(isInputCorrectData[0] && isInputCorrectData[1] && isInputCorrectData[2] )
                email = editEmail.text.toString()
                password = editPassword.text.toString()
                nickname = editNickname.text.toString()
                age = editAge.text.toString()
                gender = editGender.text.toString()

                Log.d(WSY, ""+isInputCorrectData[0] + ", " + isInputCorrectData[1] + ", " + isInputCorrectData[2] + ", " + isInputCorrectData[3] + ", " + isInputCorrectData[4])
                Log.d(WSY, email + ", " + password + ", " + nickname + ", " + age + ", " + gender)

                if(isInputCorrectData[0] && isInputCorrectData[1] && isInputCorrectData[2] && age!!.isNotEmpty() && gender!!.isNotEmpty()) {
                    signUp(email!! ,password!!, nickname!!, age!!, gender!!)
                    Log.d(WSY, "가입~")
                }
            }
        }
    }
}
