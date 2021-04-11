package com.globalwarming.earthsaver.directories

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DirectoryEntry(
    var id: String = "",
    var note: String = "",
    var answer: Boolean = false,
    var point: Long = 0L,
    var timestamp: Long = 0L
): Parcelable