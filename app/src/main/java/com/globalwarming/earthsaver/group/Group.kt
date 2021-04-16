package com.globalwarming.earthsaver.group

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    var id: String? = "",
    var name: String? = "",
    var created_by: String? = "",
    var timestamp: Long? = 0L,
    var users: List<String>? = emptyList(),
    var isAccepted: Boolean = false
) : Parcelable