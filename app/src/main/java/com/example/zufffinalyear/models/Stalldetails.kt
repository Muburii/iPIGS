package com.example.zufffinalyear.models

import android.os.Parcelable

data class Stalldetails(
    val pigGroup: String = "",
    val stallNo: String = ""
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<Stalldetails> {
        override fun createFromParcel(parcel: android.os.Parcel): Stalldetails {
            return Stalldetails(parcel)
        }

        override fun newArray(size: Int): Array<Stalldetails?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(pigGroup)
        parcel.writeString(stallNo)
    }
}