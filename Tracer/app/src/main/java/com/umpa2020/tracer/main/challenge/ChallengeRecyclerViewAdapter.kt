package com.umpa2020.tracer.main.challenge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import kotlinx.android.synthetic.main.recycler_challengefragment_item.view.*

class ChallengeRecyclerViewAdapter(var challenge: ArrayList<ChallengeData>) :
  RecyclerView.Adapter<ChallengeRecyclerViewAdapter.ItemHolder>(){


  var context: Context? = null


  override fun onBindViewHolder(holder: ItemHolder, position: Int) {

    val ChallengeData: ChallengeData = challenge.get(position)

    //holder.icons.setImageResource(ChallengeData.iconsBar!!)
    holder.name.text = ChallengeData.name
    holder.date.text = ChallengeData.date
    holder.locale.text = ChallengeData.locale

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    val itemHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_challengefragment_item, parent, false)
    context = parent.context
    return ItemHolder(itemHolder)
  }

  override fun getItemCount(): Int {
    return challenge.size
  }




  inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view){
    var icons = view.challenge_map
    var name = view.challenge_race_name
    var date = view.challenge_date
    var locale = view.challenge_locale
  }

}