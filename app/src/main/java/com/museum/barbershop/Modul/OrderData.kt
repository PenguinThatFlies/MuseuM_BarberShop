package com.museum.barbershop.Modul

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderData(
    val User_ID:String? = null,
    val Barber_ID:String? = null,
    val Barber_ID_TF:Boolean? = null,
    val Barber_Fullname:String? = null,
    val User_Fullname:String? = null,
    val Time:String? = null,
    val Day:String? = null,
    val Month:String? = null,
    val Time_ID_TF:Boolean? = false,
    val Service_1:String? = null,
    val Service_2:String? = null,
    val Service_3:String? = null,
    val Service_4:String? = null,
    val Service_5:String? = null,
    val Service_6:String? = null,
    val Service_1_TF:Boolean? = null,
    val Service_2_TF:Boolean? = null,
    val Service_3_TF:Boolean? = null,
    val Service_4_TF:Boolean? = null,
    val Service_5_TF:Boolean? = null,
    val Service_6_TF:Boolean? = null,
    val timeData: String? = null
) : Parcelable