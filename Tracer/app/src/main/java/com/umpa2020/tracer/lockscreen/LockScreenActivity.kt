package com.umpa2020.tracer.lockscreen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.TimeData
import com.umpa2020.tracer.lockscreen.util.ViewUnLock
import com.umpa2020.tracer.main.MainActivity.Companion.locationViewModel
import kotlinx.android.synthetic.main.activity_lock_screen.*

/**
 *   잠금화면 Activity
 */
class LockScreenActivity : AppCompatActivity() {
  companion object {  // 자바에서의 static과 같은 기능.
    fun newIntent(context: Context?): Intent {
      return Intent(context, LockScreenActivity::class.java)
        .apply {
          //새로운 태스크를 생성하여 그 태스크안에 엑티비티를 추가.
          //- 동일한 affinity 가 있다면 그 task 에 새 액티비티가 들어간다.
          //- 동일한 affinity 가 없다면 새로운 task 에 속한다.
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

          // 엑티비티가 새로운 태스크안에서 실행될때에 일반적으로
          // 타겟 엑티비티는 ‘최근 실행된 엑티비티’ 목록에 표시가 됩니다.
          // (이 목록은 홈버튼을 꾹 누르고 있으면 뜹니다) 이 플래그를 사용하여 실행된
          // 엑티비티는 최근실행된엑티비티 목록에서 나타나지 않습니다.
          addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
    }
  }

  // 이 메서드는 현재 Activty의 포커스 여부를 확인 시켜 주는 메서드 입니다.
  //출처: https://arabiannight.tistory.com/entry/310 [아라비안나이트]
  /**
   *  액티비티 포커스 여부를 가져와 잠금 화면 풀 스크린으로 설정해주기.
   *   https://developer.android.com/training/system-ui/immersive
   */
  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      //  window.decorView
      // 액티비티의 View 정보 구하기
      // 액티비티의 루트뷰(activity_main.xml 파일의 루트뷰) 정보를 가져올 수 있다.
      // 위 메소드는 루트뷰에 아이디가 없어도 사용할 수 있는 듯하다. http://blog.daum.net/andro_java/881
      if (hasFocus) window.decorView.systemUiVisibility = (
        View.SYSTEM_UI_FLAG_IMMERSIVE
          // Set the content to appear under the system bars so that the
          // content doesn't resize when the system bars hide and show.
          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          // Hide the nav bar and status bar
          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 네비게이션 바를 숨깁니다.
          or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }
  }


  /**
   *  onAttachedToWindow ()는 onDraw와 onMeasure 사이에 호출되는 함수이다. 윈도우에 View가 생성자를 다 실행한 후에 실행된다.
   *  onCreate()나 onResume()이 호출되지 전에 작업이 필요할 때 Override하여 사용하면 된다.
   *  [출처] onAttachedToWindow & onDetachedFromWindow 기능|작성자 ijoos
   */
  @Suppress("DEPRECATION") // 이거 하니깐 deprecation 된거 사라짐?
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    window.addFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // 창 플래그: 이 창이 사용자에게 보이는 한 장치의 화면을 켜고 밝게 유지하십시오.
        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD // deprecated 된.
        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED // deprecated 됨.
      // https://developer.android.com/reference/android/R.attr#showWhenLocked
    )

    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }

  override fun onResume() {
    super.onResume()
    setViewUnlock()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lock_screen)

    /**
     * liveData가 observe를 갖고 있지 때문에 항상 관찰 가능.
     */
    //speed랑 distance 필요
    locationViewModel.distanceSpeed.observe(this, Observer {
      lockscreenSpeedTextView.text = it.speed
      lockscreenDistanceTextView.text = it.distance
    })

    locationViewModel.times.observe(this, Observer {
      changeTimeUI(it)
    })
  }

  var flag = true
  fun changeTimeUI(records: TimeData) {


    if (flag) {
      if (records.timeControl) { // 처음 시작은 true
        lockScreenChronometer.base = records.time + records.timeWhenStop
        lockScreenChronometer.start()

      } else { // false


        lockScreenChronometer.text = records.timeText

      }
      flag = false
    }
  }


  private fun setViewUnlock() {
    lockScreenView.y = 0f
    lockScreenView.setOnTouchListener(object : ViewUnLock(this, lockScreenView) {
      override fun onFinish() {
        finish()
        super.onFinish()
      }
    })
  }
}

