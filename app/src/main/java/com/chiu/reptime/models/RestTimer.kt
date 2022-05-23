package com.chiu.reptime.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class RestTimer(
    @ColumnInfo(name = "rest_minute") val minute: Int,
    @ColumnInfo(name = "rest_second") val second: Int
) : Parcelable
