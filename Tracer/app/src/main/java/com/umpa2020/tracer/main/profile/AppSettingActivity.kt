package com.umpa2020.tracer.main.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.system.exitProcess


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
