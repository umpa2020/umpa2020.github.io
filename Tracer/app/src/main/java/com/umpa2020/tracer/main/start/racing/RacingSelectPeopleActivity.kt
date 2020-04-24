package com.umpa2020.tracer.main.start.racing

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import co.lujun.androidtagview.TagView.OnTagClickListener
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.activity_racing_select_people.*
import java.util.*

class RacingSelectPeopleActivity : AppCompatActivity() {
  val activity = this
  var likes = 0
  var mapTitle = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_select_people)

    mapTitle = intent.extras?.getString("MapTitle").toString()
    FBMapRankingRepository().getMapRanking(mapTitle, mapRankingListener)

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
      }
    })
  }

  private val mapRankingListener = object : MapRankingListener {
    override fun getMapRank(arrRankingData: ArrayList<RankingData>) {
      //레이아웃 매니저 추가
      racingSelectRecyclerView.layoutManager = LinearLayoutManager(activity)
      //adpater 추가
      racingSelectRecyclerView.adapter =
        RacingRecyclerViewAdapterMultiSelect(arrRankingData, mapTitle, tagcontainerLayout1)
    }
  }
}
