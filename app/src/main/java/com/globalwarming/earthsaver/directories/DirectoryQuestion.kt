package com.globalwarming.earthsaver.directories

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DirectoryQuestion(
    var id: String = "",
    var answer: Boolean = false,
    var category: String = "",
    var points: Long = 0L,
    var question: String = ""
): Parcelable