package com.museum.barbershop.Modul

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewData(
    val productId: String? = null,
    var imageSrc: String? = null,
    val username: String? = null,
    val email: String? = null,
    val number: String? = null,
    val password: String? = null,
    val review:String? = null
): Parcelable