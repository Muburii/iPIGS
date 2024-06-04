package com.example.zufffinalyear.models

import android.os.Parcel
import android.os.Parcelable
data class Pigdetails(
    var pigbreed: String = "",
    var tag_no: String = "",
    var litter_no: String = "",
    var gender: String = "",
    var stallNo: String? = null,
    var piggroup: String = "",
    var weight: String = "",
    var dateofbirth: String = "",
    var dateofentryonfarm: String = "",
    var fatherstagno: String = "",
    var motherstagno: String = "",
    var pigobtained: String = "",
    var reasonForArchiving: String = "",
    var dateOfEvent: String = "",
    var predictedPrice: Float = 0f,
    var notes: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?:"",
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pigbreed)
        parcel.writeString(tag_no)
        parcel.writeString(litter_no)
        parcel.writeString(gender)
        parcel.writeString(stallNo)
        parcel.writeString(piggroup)
        parcel.writeString(weight)
        parcel.writeString(dateofbirth)
        parcel.writeString(dateofentryonfarm)
        parcel.writeString(fatherstagno)
        parcel.writeString(motherstagno)
        parcel.writeString(pigobtained)
        parcel.writeString(reasonForArchiving)
        parcel.writeString(dateOfEvent)
        parcel.writeFloat(predictedPrice)
        parcel.writeString(notes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pigdetails> {
        override fun createFromParcel(parcel: Parcel): Pigdetails = Pigdetails(parcel)
        override fun newArray(size: Int): Array<Pigdetails?> = arrayOfNulls(size)
    }
}

