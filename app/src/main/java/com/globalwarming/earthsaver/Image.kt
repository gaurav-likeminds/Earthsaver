package com.globalwarming.earthsaver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var id: String = "",
    var url: String= "",
    var title: String = "",
    var timestamp: Long = 0L
): Parcelable