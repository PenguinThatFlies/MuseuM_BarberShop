package com.museum.barbershop.Modul

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeData(
    val time:String? = null
) : Parcelable

val times = listOf<TimeData>(
    TimeData("10:00"),
    TimeData("11:00"),
    TimeData("12:00"),
    TimeData("13:00"),
    TimeData("14:00"),
    TimeData("15:00"),
    TimeData("16:00"),
    TimeData("17:00"),
    TimeData("18:00")
)