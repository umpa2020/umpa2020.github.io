package com.umpa2020.tracer.util

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.umpa2020.tracer.App
import java.util.*

object TTS {
  lateinit var tts: TextToSpeech

  init {
    tts = TextToSpeech(App.instance.context(), TextToSpeech.OnInitListener {
      if (it == TextToSpeech.SUCCESS) {
        //사용할 언어를 설정
        val result = tts.setLanguage(Locale.getDefault())
        //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Toast.makeText(App.instance.context(), "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT)
            .show()
        } else {
          //음성 톤
          tts.setPitch(0.7f)
          //읽는 속도
          tts.setSpeechRate(1.2f)
        }
      }
    })
  }

  fun speech(sentence: String) {
    // Get the preferences
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.instance.context())
    if (prefs.getBoolean("ttsNotificationSetting", false)) {
      tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null)
      Logg.d(sentence)
    }
  }
}