package com.museum.barbershop.Modul.AuthModul

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpData(
    val productId: String? = null,
    var imageSrc: String? = null,
    val username: String? = null,
    val email: String? = null,
    val number: String? = null,
    val password: String? = null,
    var star:Int? = null,
    var message: String? = null
): Parcelable