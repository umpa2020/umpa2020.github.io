package com.umpa2020.tracer.main.challenge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.recycler_localname_item.view.*


class localAdapter(var local: Array<String>) :
  RecyclerView.Adapter<localAdapter.ItemHolder>() {
  var context: Context? = null

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    holder.name.text = local.get(position)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    val itemHolder = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_localname_item, parent, false)
    context = parent.context
    return ItemHolder(itemHolder)
  }

  override fun getItemCount(): Int {
    return local.size
  }

  inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    var name = view.local_name
  }
}