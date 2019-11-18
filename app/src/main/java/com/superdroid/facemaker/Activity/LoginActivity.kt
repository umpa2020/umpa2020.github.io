package com.superdroid.facemaker.Activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.superdroid.facemaker.R
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

//import androidx.test.orchestrator.junit.BundleJUnitUtils.getResult



class LoginActivity : Activity() {
    // for google
    private val RC_SIGN_IN = 900
    private var googleSignInClient: GoogleSignInClient? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var buttonGoogle: SignInButton? = null

    // for E-mail
    private val PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$")
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null

    var email: String = "";
    var password: String = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loadingintent = Intent(this, LoadingActivity::class.java)
        startActivity(loadingintent)

        //googleSignInClient?.signOut()

        buttonGoogle = findViewById(R.id.btn_googleSignIn)
        firebaseAuth = FirebaseAuth.getInstance()

        editTextEmail = login_id
        editTextPassword = login_password

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)




        buttonGoogle?.setOnClickListener {
            val signInIntent = googleSignInClient

            if (signInIntent != null) {
                startActivityForResult(signInIntent.signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Toast.makeText(this, task.toString(), Toast.LENGTH_LONG).show()

            try {
                // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    firebaseAuthWithGoogle(account)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                }
            } catch (e: ApiException) {
            }

        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        var nextIntent = Intent(this, MainActivity::class.java)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this, object :
            OnCompleteListener<AuthResult> {

            override fun onComplete(task: Task<AuthResult>) {

                if (task.isSuccessful()) {
                    // 로그인 성공

                    nextIntent.putExtra("id", acct.email)
                  //  finish()
                    startActivity(nextIntent)

                } else {
                    // 로그인 실패
                }

            }
        })
    }


    fun signUp(view:View) {
        email = editTextEmail?.text.toString();
        password = editTextPassword?.text.toString();
        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }
    }


    fun signIn(view:View) {
        email = editTextEmail?.getText().toString();
        password = editTextPassword?.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            loginUser(email, password);
        }
    }

    private fun isValidEmail():Boolean {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPasswd():Boolean{
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else return PASSWORD_PATTERN.matcher(password).matches()
    }

    private fun createUser(email:String, password:String) {
        firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val user = firebaseAuth?.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()


                }
            }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                    val user = firebaseAuth?.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id", login_id.text.toString())
                    startActivity(intent)

                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()

                }

            }
    }


}
