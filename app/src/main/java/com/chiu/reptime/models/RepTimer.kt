package com.chiu.reptime.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepTimer(
    @ColumnInfo(name="rep_hour") val hour: Int,
    @ColumnInfo(name="rep_minute") val minute: Int,
    @ColumnInfo(name="rep_second") val second: Int
) : Parcelable
