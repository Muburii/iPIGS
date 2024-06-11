package com.example.zufffinalyear.models

import android.os.Parcel
import android.os.Parcelable

data class Stalldetails(
    val pigGroup: String = "",
    val stallNo: String = "",
    var numberOfPigs: Int = 0,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pigGroup)
        parcel.writeString(stallNo)
        parcel.writeInt(numberOfPigs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stalldetails> {
        override fun createFromParcel(parcel: Parcel): Stalldetails {
            return Stalldetails(parcel)
        }

        override fun newArray(size: Int): Array<Stalldetails?> {
            return arrayOfNulls(size)
        }
    }
}
