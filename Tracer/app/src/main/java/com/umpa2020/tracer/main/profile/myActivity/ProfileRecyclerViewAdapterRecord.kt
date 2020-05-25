package com.umpa2020.tracer.main.profile.myActivity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.RACE_RESULT
import com.umpa2020.tracer.constant.Constants.Companion.RANKING_DATA
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.main.start.challengeracing.ChallengeRacingFinishActivity
import com.umpa2020.tracer.main.start.racing.RacingFinishActivity
import com.umpa2020.tracer.network.BaseFB.ActivityMode.*
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBChallengeRepository
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_profile_user_record_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

class ProfileRecyclerViewAdapterRecord(val datas: MutableList<ActivityData>) :
  RecyclerView.Adapter<ProfileRecyclerViewAdapterRecord.MyViewHolder>(), CoroutineScope by MainScope() {
  var context: Context? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_profile_user_record_item, parent, false)
    context = parent.context
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int {
    return datas.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val activityData = datas[position]

    launch {
      holder.mapImageView.image(FBMapRepository().getMapImage(activityData.mapId!!))
      FBMapRepository().getMapTitle(activityData.mapId)?.let {
        val time = activityData.time!!.toLong().format(Y_M_D)
        when (activityData.mode) {
          RACING_SUCCESS -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.racing_go_the_distance), it, time)
          }
          RACING_FAIL -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.racing_fail), it, time)
          }
          MAP_SAVE -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.map_save), it, time)
          }
          CHALLENGE -> {
            holder.activityText.text =
              String.format(context!!.getString(R.string.activity_challenge), it, time)
          }
        }
      }
    }

    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        when (activityData.mode) {
          MAP_SAVE -> {
            val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
            nextIntent.putExtra(MAP_ID, activityData.mapId) //mapTitle 정보 인텐트로 넘김
            context!!.startActivity(nextIntent)
          }
          RACING_SUCCESS -> {
            launch {
              val nextIntent = Intent(context, RacingFinishActivity::class.java).apply {
                putExtra(MAP_ID, activityData.mapId) //mapTitle 정보 인텐트로 넘김
                putExtra(RACE_RESULT, true)
                val rankingData = FBRacingRepository().getRankingData(activityData.dataRef!!)
                putExtra(RANKING_DATA, rankingData)
              }
              context!!.startActivity(nextIntent)
            }

          }
          RACING_FAIL -> {
            context!!.toast("실패한 경기는 조회할 수 없습니다")
          }
          CHALLENGE -> {
            launch {
              val challengeRecordData = FBChallengeRepository().getChallengeRecord(activityData.dataRef!!)
              val nextIntent = Intent(context, ChallengeRacingFinishActivity::class.java).apply {
                putExtra(Constants.CHALLENGE_ID, activityData.mapId)
                putExtra(Constants.RACING_DISTANCE, activityData.mapId)
                putExtra("RecordList", challengeRecordData!!.recordList!!.toLongArray())
                putExtra("BestList", challengeRecordData.bestList!!.toLongArray())
                putExtra("WorstList", challengeRecordData.worstList!!.toLongArray())
              }
              context!!.startActivity(nextIntent)
            }
          }
          null -> {
            //do nothing
          }
        }

      }
    })
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var mapImageView = view.profileUserActivityMapImageView!!
    var activityText = view.profileUserActivityTextView!!
  }
}

