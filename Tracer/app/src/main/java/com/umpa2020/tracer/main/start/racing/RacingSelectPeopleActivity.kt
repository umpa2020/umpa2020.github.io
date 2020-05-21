package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.lujun.androidtagview.TagView.OnTagClickListener
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RacerData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.addDirectionSign
import com.umpa2020.tracer.extensions.classToGpx
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.main.start.racing.RacingActivity.Companion.ROUTE_GPX
import com.umpa2020.tracer.main.start.running.RunningSaveActivity
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_racing_select_people.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class RacingSelectPeopleActivity : AppCompatActivity(), OnSingleClickListener {
  val activity = this
  var likes = 0
  var mapId = ""
  lateinit var routeGPX: RouteGPX
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_select_people)

    mapId = intent.getStringExtra(MAP_ID)!!

    val routeGPXUri = intent.getStringExtra(ROUTE_GPX)
    routeGPX = Uri.parse(routeGPXUri).gpxToClass()

    MainScope().launch {
      FBMapRepository().listMapRanking(mapId).let {
        //레이아웃 매니저 추가
        rankingDataList = it
        racingSelectRecyclerView.layoutManager = LinearLayoutManager(activity)
        //adpater 추가
        racingSelectRecyclerView.adapter =
          RacingRecyclerViewAdapterMultiSelect(it, mapId, tagcontainerLayout1)
      }
    }


    // Set custom click listener
    tagcontainerLayout1.setOnTagClickListener(object : OnTagClickListener {
      override fun onTagClick(position: Int, text: String) {
        //클릭하면 아이템 삭제
        if (position < tagcontainerLayout1.childCount) {
          tagcontainerLayout1.removeTag(position)
          //whichIsCheck에서 text지움
          (racingSelectRecyclerView.adapter as RacingRecyclerViewAdapterMultiSelect).whichIsCheck.remove(text)
          racingSelectRecyclerView.adapter!!.notifyDataSetChanged()
        }
      }

      override fun onTagLongClick(position: Int, text: String) {}

      override fun onSelectedTagDrag(position: Int, text: String) {}


      override fun onTagCrossClick(position: Int) {
        //mTagContainerLayout1.removeTag(position);
        //클릭하면 아이템 삭제
        if (position < tagcontainerLayout1.childCount) {
          tagcontainerLayout1.removeTag(position)
          //whichIsCheck에서 text지움
          (racingSelectRecyclerView.adapter as RacingRecyclerViewAdapterMultiSelect).whichIsCheck.removeAt(position)
          racingSelectRecyclerView.adapter!!.notifyDataSetChanged()
        }
      }

    })

    racingSelectButton.setOnClickListener(this)
  }

  lateinit var rankingDataList: MutableList<RankingData>

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.racingSelectButton -> {
        val racerList = tagcontainerLayout1.tags.toTypedArray().map { nickName ->
          RacerData(
            rankingDataList.find { it.challengerNickname == nickName }?.challengerId!!, nickName
          )
        }
        val intent = Intent(App.instance.context(), RacingActivity::class.java)

        val saveFolder = File(App.instance.filesDir, "routeGPX") // 저장 경로
        if (!saveFolder.exists()) {       //폴더 없으면 생성
          saveFolder.mkdir()
        }
        routeGPX.addDirectionSign()
        val routeGpxUri = routeGPX.classToGpx(saveFolder.path).toString()
        intent.putExtra(ROUTE_GPX, routeGpxUri)
        intent.putExtra(RACER_LIST, racerList.toTypedArray())
        intent.putExtra(MAP_ID, mapId)
        startActivity(intent)
      }
    }
  }

  companion object {
    const val RACER_LIST = "RacerList"
  }
}
