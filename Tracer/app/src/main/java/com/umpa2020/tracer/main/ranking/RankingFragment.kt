package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.umpa2020.tracer.R
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.main.MainActivity.Companion.MESSENGER_INTENT_KEY
import com.umpa2020.tracer.main.MainActivity.Companion.WSY
import com.umpa2020.tracer.network.getRanking
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment() {
  lateinit var strDate: String
  lateinit var location: Location
  var mHandler: IncomingMessageHandler? = null


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    //TODO: return inflate~~~
    //TODO: Thread 사용하지 말고, 클래스로 빼서 getInfos 처럼 하면 배열이 받아온다는 걸 미리 알 수 있게
    //TODO: activity Created 로 이전
    val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

    mHandler = IncomingMessageHandler()

    //getRanking().getExcuteDESCENDING(context!!, view, location)


    /*
    view.test_button3.setOnClickListener {
      getRanking().getFilterRange(view, location)
    }

     */


    //필터 버튼 누르면 레이아웃 보임
    view.rankingToolBarTuneButton.setOnClickListener{
      appearAnimation()
    }

    //필터 레이아웃 밖 영역 클릭하면 사라짐
    view.disappearLayout.setOnClickListener{
      disappearAnimation()
    }


    //전체 삭제 누를 때
    view.allDeleteButton.setOnClickListener {
      view.tuneRadioBtnExecute.isChecked = false
      view.tuneRadioBtnLike.isChecked = false
      view.progressTextView.text = "0"
      view.seekBar.progress = 0
    }

    //적용 버튼 누를때
    view.applyButton.setOnClickListener{
      var tuneDistance = Integer.parseInt(view.progressTextView.text.toString())
      Log.d("test", tuneDistance.toString())

      //실행순 버튼에 체크가 되어 있을 경우
      if(view.tuneRadioBtnExecute.isChecked){
        getRanking().getExcuteDESCENDING(context!!, view, location)
      }

      else{
        //TODO 좋아요 버튼 누를 때 실행되는 코드 추가
      }

      //TODO : tuneDistance에 거리 값 넣어놨으니 이대로 필터 적용

      disappearAnimation()
    }

    view.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        view.progressTextView.text = i.toString()

        if(i==100){
          view.progressTextView.text = "100+"
        }
      }
      override fun onStartTrackingTouch(seekBar: SeekBar) {}

      override fun onStopTrackingTouch(seekBar: SeekBar) {}
    })




    return view
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    mHandler = IncomingMessageHandler()
    Intent(context, LocationBackgroundService::class.java).also {
      val messengerIncoming = Messenger(mHandler)
      it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)

      activity!!.startService(it)
    }
  }

  inner class IncomingMessageHandler : Handler() {
    override fun handleMessage(msg: Message) {

      super.handleMessage(msg)

      when (msg.what) {
        LocationBackgroundService.LOCATION_MESSAGE -> {
          location = msg.obj as Location
          Log.d(WSY, "RankingFragment : $location")

        }
      }
    }
  }

  /**
   * 레이아웃이 스르륵 보이는 함수
   */

  private fun appearAnimation(){
    val animate = AlphaAnimation(0f,1f) //투명도 변화
    animate.duration = 500
    animate.fillAfter = true
    view!!.tuneLinearLayout.visibility = View.VISIBLE
    view!!.tuneLinearLayout.startAnimation(animate)
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  private fun disappearAnimation(){
    val animate = AlphaAnimation(1f,0f)
    animate.duration = 500
    animate.fillAfter = true
    view!!.tuneLinearLayout.visibility = View.GONE
    view!!.tuneLinearLayout.startAnimation(animate)
  }





}