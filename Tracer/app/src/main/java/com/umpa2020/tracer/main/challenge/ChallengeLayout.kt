package com.umpa2020.tracer.main.challenge

import android.os.Parcel
import android.os.Parcelable

class ChallengeLayout(var image: Int, var text: String?) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString()
  ) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(image)
    parcel.writeString(text)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ChallengeLayout> {
    override fun createFromParcel(parcel: Parcel): ChallengeLayout {
      return ChallengeLayout(parcel)
    }

    override fun newArray(size: Int): Array<ChallengeLayout?> {
      return arrayOfNulls(size)
    }
  }
}