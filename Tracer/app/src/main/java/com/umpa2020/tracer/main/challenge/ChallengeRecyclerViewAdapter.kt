package com.umpa2020.tracer.main.challenge

import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.umpa2020.tracer.R

class ChallengeRecyclerViewAdapter(private val getContext : Context, private val CustomLayout : Int, private val custom_item : ArrayList<ChallengeLayout>)
  : ArrayAdapter<ChallengeLayout>(getContext, CustomLayout,custom_item), Parcelable {

  constructor(parcel: Parcel) : this(
    TODO("getContext"),
    parcel.readInt(),
    TODO("custom_item")
  ) {
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    var row = convertView
    val Holder : ViewHolder
    if(row == null) {
      val inflater = (getContext as Activity).layoutInflater
      row = inflater.inflate(CustomLayout, parent, false)
      Holder = ViewHolder()
      Holder.img = row!!.findViewById(R.id.challenge_map) as ImageView
      Holder.txt = row!!.findViewById(R.id.challenge_map_text) as TextView
      row.setTag(Holder)
    } else {
      Holder = row.getTag() as ViewHolder
    }
    val item = custom_item[position]
    Holder.img!!.setImageResource(item.image)
    Holder.txt!!.setText(item.text)
    return row
  }
  class ViewHolder{
    internal var img : ImageView? = null
    internal var txt : TextView? = null
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(CustomLayout)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ChallengeRecyclerViewAdapter> {
    override fun createFromParcel(parcel: Parcel): ChallengeRecyclerViewAdapter {
      return ChallengeRecyclerViewAdapter(parcel)
    }

    override fun newArray(size: Int): Array<ChallengeRecyclerViewAdapter?> {
      return arrayOfNulls(size)
    }
  }
}