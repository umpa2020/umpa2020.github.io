package com.umpa2020.tracer.login.join

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jakewharton.rxbinding2.widget.RxTextView
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private var WSY = "WSY"

    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화 합니다.
    internal val viewDisposables = CompositeDisposable()

    private lateinit var inputDataField: Array<EditText>
    private lateinit var textInputLayoutArray: Array<TextInputLayout>
    private lateinit var inputInfoMessage: Array<String>
    private var isInputCorrectData: Array<Boolean> = arrayOf(false, false, false)

    // 카메라 requestCode
    private val PICK_FROM_ALBUM = 1

    private var bitmapImg: Bitmap? = null
    private var basicBitmapImg: Bitmap? = null

    private var nickname: String? = null
    private var age: String? = null
    private var gender: String? = null

    // firebase DB
    private var mFirestoreDB: FirebaseFirestore? = null

    //  firebase Storage
    private var mStorage: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null

    // 중복 확인 버튼
    private var redundantCheckButton: Button? = null

    // 초기 가입자인 경우 LoginActivity에서 tokenId, email을 넘겨 받음
    private var tokenId: String? = null
    private var email: String? = null

    var timestamp: Long = 0
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sign_up)

        progressbar = ProgressBar(this)

        editNickname.requestFocus()
        init()

        editNickname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                flag = 3
                Log.d(WSY, flag.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun init() {
        /**
         *  Firestore 초기화
         */

        mFirestoreDB = FirebaseFirestore.getInstance()

        /**
         *  Firebase Storage 초기화
         */

        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference

        /**
         *  회원가입 Input관련 초기화
         */
        inputDataField = arrayOf(editNickname, editAge, editGender)
        textInputLayoutArray =
            arrayOf(nicknameTextInput, ageTextInput, genderTextInput)
        inputInfoMessage = arrayOf(getString(R.string.txtInputInfoNick), getString(R.string.txtInputInfoAge), getString(R.string.txtInputInfoGender))

        typingListener()

        /**
         *  중복 확인 버튼 초기화
         */
        redundantCheckButton = redundantCheckButton

        /**
         *   초기 가입자인 경우 LoginActivity에서 tokenId, email을 넘겨 받음
         */
        var intent = intent
        tokenId = intent.getStringExtra("tokenId")
        email = intent.getStringExtra("email")
    }

    /**
     * 카메라 접근 권한 and 앨범 접근
     */
    private fun goToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            var length = permissions.size
            for (i in 0 until length - 1) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d(WSY, "권한 허용 : " + permissions[i])
                    goToAlbum()
                }
            }
        }
    }

    /**
     *  앨범 접근 권한
     */
    private fun checkSelfPermission() {
        var temp = ""

        // 파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
        }

        Log.d(WSY, temp)
        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" ").toTypedArray(), 1)
        } else {
            // 모든 허용 상태
            //toast("권한을 모두 허용")
            goToAlbum()
        }
    }


    /**
     * 각 필드별 회원가입 조건이 맞는지 비동기 체크
     */
    private fun typingListener() {
        //Nickname
        val disposableNick = RxTextView.textChanges(inputDataField[0])
            .map { t -> t.isEmpty() || !Pattern.matches(Constants.NICKNAME_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(0, it)
            }) {
                //Error Block
            }

        val disposableAge = RxTextView.textChanges(inputDataField[1])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.AGE_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                reactiveInputTextViewData(1, it)
            }) {
                //Error Block
            }

        val disposableGender = RxTextView.textChanges(inputDataField[2])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.GENDER_RULS, t) }
            .subscribe({ it ->
                //inputDataField[2].setText("")
                Log.d(WSY, "성별 : " + it.toString())
                reactiveInputTextViewData(2, it)
            }) {
                //Error Block
            }
        viewDisposables.addAll(
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

    var options: BitmapFactory.Options? = null

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
            if (resultCode == RESULT_OK) {
                try {
                    var inputStream = intentData!!.data?.let { getContentResolver().openInputStream(it) }

                    // 프로필 사진을 비트맵으로 변환
                    options = BitmapFactory.Options()
                    options!!.inSampleSize = 2
                    bitmapImg = BitmapFactory.decodeStream(inputStream, null, options)
                    inputStream!!.close()

                    profileImage.setImageBitmap(bitmapImg)
                    profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
                } catch (e: java.lang.Exception) {

                }
            } else if (resultCode == RESULT_CANCELED) {
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
    private fun signUp(bitmapImg: Bitmap, nickname: String, age: String, gender: String) {
        if (flag == 1) {// false이면 닉네임 체크가 안된 것. 그러면 실행되면 안돼
            val dt = Date().time
            uploadProfileImage(bitmapImg, nickname, dt.toString())
            uploadUserInfo(nickname, age, gender, dt.toString())
        } else if (flag == 3) {
            textInputLayoutArray[0].setErrorTextColor(resources.getColorStateList(R.color.red, null))
            textInputLayoutArray[0].error = "중복 확인을 해주십시오."
            flag = 3
        }
    }

    var flag = 3 // 닉네임 체크를 위한 flag
    private fun nicknameCheck(nickname: String) {
        // 서버랑 닉네임 체크

        /**
         *  더 좋은 방법이 있을 지 모르지만 whereEqualTo는 db에 없는 것에 대한 검사는 안하는 듯
         *  그래서 flag로 억지로 유무 판단.
         */
        mFirestoreDB!!.collection("userinfo").whereEqualTo("nickname", nickname).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(WSY, document.id)
                    Log.d(WSY, document.exists().toString())
                    if (document.exists()) {
                        Log.d(WSY, "${document.id} => ${document.data}")
                        textInputLayoutArray[0].setErrorTextColor(resources.getColorStateList(R.color.red, null))
                        textInputLayoutArray[0].error = "이미 사용중인 닉네임입니다."
                        flag = 2 // flag = false로 하여 addOnCompleteListener의 if문이 실행 안되게 하기
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(WSY, "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                if (flag == 3) {
                    textInputLayoutArray[0].setErrorTextColor(resources.getColorStateList(R.color.yellowGreen, null))
                    textInputLayoutArray[0].error = "사용 가능한 닉네임입니다."

                    flag = 1
                }
            }
        flag = 3 // 비동기라서 이건 무조건 실행. 하지만 firebase보단 항상 먼저 실행됨.
    }

    private fun uploadProfileImage(bitmapImg: Bitmap, nickname: String, dt: String) {
        // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜(영어).jpg 경로 만들기

        val profileRef = mStorageReference!!.child("Profile").child(tokenId!!).child(dt + ".jpg")
        // 이미지
        val bitmap = bitmapImg
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()
        var uploadTask = profileRef.putBytes(imageData)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            UserInfo.autoLoginKey = tokenId!!
            Log.d("ssmm11" , "tokenID = " +tokenId)
            UserInfo.email = email!!
            UserInfo.nickname = nickname // Shared에 nickname저장.
            var nextIntent = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(nextIntent)
            progressbar.dismiss()
            finish()
        }
    }

    private fun uploadUserInfo(nickname: String, age: String, gender: String, dt: String) {
        // 회원 정보
        val data = hashMapOf(
            "googleTokenId" to tokenId,
            "nickname" to nickname,
            "age" to age,
            "gender" to gender,
            "profileImagePath" to dt+".jpg"
        )
        mFirestoreDB!!.collection("userinfo").document(tokenId!!).set(data)
            .addOnSuccessListener { Log.d(WSY, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(WSY, "Error writing document", e) }

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.backImageBtn -> {
                finish()
            }
            R.id.editAge -> {

            }
            R.id.editGender -> {
                var intent = Intent(this, GenderSelectActivity::class.java)
                startActivityForResult(intent, 102)
                editAge.clearFocus()
                editNickname.clearFocus()
            }
            R.id.profileImage -> {
                // tedPermission()
                checkSelfPermission()
            }
            R.id.redundantCheckButton -> {
                nickname = editNickname.text.toString()
                if (isInputCorrectData[0]) {
                    nicknameCheck(nickname!!)
                } else {
                    if (!isInputCorrectData[0])
                        reactiveInputTextViewData(0, false)
                }

            }
            R.id.sign_up_button -> {

                nickname = editNickname.text.toString()
                age = editAge.text.toString()
                gender = editGender.text.toString()


                Log.d(WSY, "가입 버튼 눌렀을 때" + nickname + ", " + age + ", " + gender)

                if(textInputLayoutArray[0].error != "사용 가능한 닉네임입니다."){ //무조건 중복 확인 버튼을 눌러야만 회원가입 가능하게 함
                    Log.d("sujin", textInputLayoutArray[0].error.toString() + "if문 검사")
                    Toast.makeText(this, "중복 확인을 해주세요.", Toast.LENGTH_LONG).show()
                }
                else{
                    if (isInputCorrectData[0] && age!!.isNotEmpty() && gender!!.isNotEmpty()) {
                        if (bitmapImg == null) // 프로필 이미지를 설정하지 않았을 때 = 사용자 입장에서 프로필 버튼을 누르지 않았음
                        {
                            showSettingPopup() //팝업창으로 물어봄
                        } else { // 프로필 이미지 있는 경우 설정한 프로필로 가입하기
                            signUp(bitmapImg!!, nickname!!, age!!, gender!!)
                            progressbar.show() //메인창으로 넘어가기 전까지 프로그래스 바 띄움
                        }
                    }
                    // 정보가 하나라도 입력 안되면 error 메시지 출력
                    else {
                        for (a in 0..2) {
                            // 에러 메시지 중에서도 정보가 입력되것 제외하고 출력.
                            if (!isInputCorrectData[a])
                                reactiveInputTextViewData(a, false)
                        }
                    }
                }

            }

        }

    }

    /**
    기본 이미지로 설정할건지 물어보는 팝업
     **/
    fun showSettingPopup(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.sign_up_activity_yesnopopup, null)
        val textView: TextView = view.findViewById(R.id.signUpActivityPopUpTextView)
        textView.text = "기본 이미지로 설정하시겠습니까?"

        val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
            .setTitle("선택해주세요.")
            .create()

        //Yes 버튼 눌렀을 때
        val yesButton = view.findViewById<Button>(R.id.signUpActivityYesButton)
        yesButton.setOnClickListener {
            //기본 이미지로 설정
            basicBitmapImg = BitmapFactory.decodeResource(resources, R.drawable.basic_profile)
            signUp(basicBitmapImg!!, nickname!!, age!!, gender!!)
            alertDialog.dismiss()
            progressbar.show() //기본이미지로 회원가입이 바로 진행되도록 프로그레스바 띄움
        }

        //No 버튼 눌렀을 때
        val noButton = view.findViewById<Button>(R.id.signUpActivityNoButton)
        noButton.setOnClickListener {
            //갤러리에서 원하는 프로필 이미지 선택할 수 있도록 권한체크
            alertDialog.dismiss()
            checkSelfPermission()
        }

        alertDialog.setView(view)
        alertDialog.show() //팝업 띄우기
    }
}