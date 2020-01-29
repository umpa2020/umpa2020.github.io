package com.korea50k.tracer.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chibatching.kotpref.Kotpref
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.Join.SignUpActivity
import com.korea50k.tracer.MainActivity
import com.korea50k.tracer.R
import com.korea50k.tracer.UserInfo
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    val WSY = "WSY"

    // firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private var signInButton: SignInButton? = null

    // firebase DB
    private var mFirestoreDB : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /**
         *  Firestore 관련 코드
         */

        mFirestoreDB = FirebaseFirestore.getInstance()


        /**
         *  Google signIn 코드 구성.
         *  서버의 클라이언트 ID를 requestIdToken 메서드에 전달해야 함. OAuth 2.0 클라이언트 ID
         *  a. GCP Console에서 사용자 인증 정보 페이지를 엽니다.
         *  b. 웹 애플리케이션 유형의 클라이언트 ID가 백엔드 서버의 OAuth 2.0 클라이언트 ID입니다.
         */
        signInButton =
            googleSignInButton // googleSignInButton 사용. Gradle에서 implementation을 해줘야 사용 가능.


        //  Configure Google Sign In
        // 구글 로그인 옵션
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions) //구글 로그인 클래스
        mAuth = FirebaseAuth.getInstance() // FirebaseAuth를 사용하기 위해서 인스턴스를 꼭 받아오기
        Kotpref.init(this) // Kotpref 사용을 위한 singleton context 저장

        // 람다식으로 onClick 설정
        googleSignInButton.setOnClickListener {
            signIn()
        }


    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN /*&&  resultCode == RESULT_OK*/) {

            // 구글 로그인에 성공했을때 넘어오는 토큰값을 가지고 있는 Task
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Log.d(WSY, "여기까지 실행??")
                // Google Sign In was successful, authenticate with Firebase
                // 구글 로그인 성공
                // ApiException 캐스팅
                /**
                 *   var account = task.getResult(ApiException::class.java)
                 *   이 부분에서 오류가 났었는데
                 *   https://stackoverflow.com/questions/47437678/why-do-i-get-com-google-android-gms-common-api-apiexception-10
                 *   firebase SHA-1 키 등록이 안되어 있어서 오류가 남
                 *   추가 등록을 해줘서 해결은 했는데 이런 방식이 맞는지 모르겠음...
                 */
                var account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(WSY, "Google sign in failed", e)
                // ...
            }
        }

    }


    /**
     *  사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID 토큰을 가져와서
     *  Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase 인증을 받습니다.
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(WSY, "firebaseAuthWithGoogle:" + acct.id!!) // firebaseAuthWithGoogle:117635384468060774340 => 계정 고유 번호 => 이것을 Shared에 저장하여 자동 로그인 구현
        Log.d(WSY, acct.email)
        UserInfo.autoLoginKey =  acct.id!!.toString()
        UserInfo.email = acct.email.toString()
        // Credentail 구글 로그인에 성공했다는 인증서
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Log.d(WSY, credential.toString())

        //인증서를 Firebase에 넘겨줌(구글 사용자가 등록)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // 성공하면
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(WSY, "signInWithCredential:success")
                    Log.d(WSY, UserInfo.autoLoginKey) // 로그인에 성공해서 토큰id저장 후 불러오기 // 117635384468060774340
                    // 로그인 성공
                    mFirestoreDB!!.collection("UserInfo").document(acct.email!!).get()
                        .addOnCompleteListener{task ->
                            if (task.isSuccessful) {
                                // Document found in the offline cache
                                val document = task.result
                                Log.d(WSY,document.toString()) // DocumentSnapshot{key=UserInfo/117635384468060774340, metadata=SnapshotMetadata{hasPendingWrites=false, isFromCache=false}, doc=null}
                                Log.d(WSY,document!!.id)  // 117635384468060774340
                                Log.d(WSY,document.exists().toString()) // false
                                Log.d(WSY,document.reference.toString()) // com.google.firebase.firestore.DocumentReference@aafeaf20
                                Log.d(WSY, "Cached document data: ${document?.data}") // Cached document data: null

                                if("${document?.data}" == "null") // data가 없으면 SignUpActivity로 이동
                                {
                                    // 최초 가입자
                                    Log.d(WSY, "초기 가입인가")
                                    var nextIntent = Intent(this@LoginActivity, SignUpActivity::class.java)
                                    startActivity(nextIntent)
                                    finish()
                                } else { // data가 있으면 Main으로
                                    Log.d(WSY, "기존 가입자 -> 메인으로")
                                    var nextIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(nextIntent)
                                    finish()
                                }
                            } else {
                                Log.d(WSY, "Cached get failed: ", task.exception)
                            }

                        }
                    // 1. 회원 정보가 없으면 초기 가입자이므로 정보 입력받는 창으로
                    // 2. 로그아웃을 했을 경우 회원 정보를 받은 기록이 있는지 판단하고 있으면 메인 화면으로
                    //    이때 db에서 값을 가져와야겠지??
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(WSY, "signInWithCredential:failure", task.exception)
                    // Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                 //   updateUI(null)
                }

                // ...
            }
    }

//    /**
//     *  로그인 후에 MainActivity로 intent로 옮기겠다
//     */
//    private fun updateUI(user: FirebaseUser?) { //update ui code here
//        if (user != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }

}
