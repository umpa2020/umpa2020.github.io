package com.umpa2020.tracer.main.profile.settting

import android.os.Bundle
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.R

class AppSettingActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_app_setting)

    supportFragmentManager
      .beginTransaction()
      .replace(R.id.preferenceSettingFrag, SettingPreferenceFragment())
      .commit()
  }
}
