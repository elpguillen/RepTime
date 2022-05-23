package com.chiu.reptime.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded val repTimer: RepTimer,
    @Embedded val restTimer: RestTimer,
    @ColumnInfo(name="number_reps") val numberReps: Int,
    @NonNull val name: String
)
