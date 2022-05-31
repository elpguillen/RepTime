package com.chiu.reptime.data

import androidx.annotation.NonNull
import androidx.room.*
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer

@Entity(
    tableName = "workouts",
    indices = [Index(value=["name"], unique = true)])
data class Workout(
    @PrimaryKey(autoGenerate = true) // by default it starts at '1'
    val id: Int = 0,
    @Embedded val repTimer: RepTimer,
    @Embedded val restTimer: RestTimer,
    @ColumnInfo(name="number_reps") val numberReps: Int,
    @NonNull val name: String
)
