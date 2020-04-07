package com.umpa2020.tracer.main.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.replace
import com.google.firebase.auth.FirebaseAuth
import com.umpa2020.tracer.R

class AppSettingActivity : AppCompatActivity() {
  // firebase Auth
  private var mAuth: FirebaseAuth? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_app_setting)

    supportFragmentManager
      .beginTransaction()
      .replace(R.id.preferenceSettingFrag, SettingPreferenceFragment())
      .commit()
  }
}
