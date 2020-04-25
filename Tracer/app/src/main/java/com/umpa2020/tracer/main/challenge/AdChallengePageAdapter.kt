package com.umpa2020.tracer.main.challenge

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.demono.adapter.InfinitePagerAdapter
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.AdChallengeData
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.fragment_adchallenge.view.*

class AdChallengePageAdapter(
  val adChallengeList: ArrayList<AdChallengeData>,
  val context: Context
) : InfinitePagerAdapter() {
  override fun getItemCount(): Int {
    return adChallengeList.size
  }

  override fun getItemView(position: Int, convertView: View?, container: ViewGroup?): View {
    val view = LayoutInflater.from(container!!.context)
      .inflate(R.layout.fragment_adchallenge, container, false)
    view.adChallengeImgView.setImageDrawable(context.getDrawable(adChallengeList[position].img))
    view.setOnClickListener {
      Logg.d(position.toString())
        val intent = Intent(context,ChallengeRecycleritemClickActivity::class.java)
        intent.putExtra("challengeId",adChallengeList[position].challengeId)
        context.startActivity(intent)
    }
    return view
  }

}