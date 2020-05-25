package com.umpa2020.tracer.main

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 *  액티비티의 공통 기능 정의
 */
open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

  override fun onPause() {
    super.onPause()
    MainScope().cancel()
  }
}
