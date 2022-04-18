package com.chiu.reptime.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepTimer(
    val hour: Int,
    val minute: Int,
    val second: Int
) : Parcelable
