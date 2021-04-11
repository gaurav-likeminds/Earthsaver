package com.globalwarming.earthsaver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var age: Long = 0,
    var email: String = "",
    var gender: String = "",
    var location: String = "",
    var name: String = "",
    var points: Long = 0L
) : Parcelable