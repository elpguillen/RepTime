package com.chiu.reptime

import android.app.Application
import com.chiu.reptime.data.WorkoutRepository
import com.chiu.reptime.data.WorkoutRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WorkoutApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val workoutDatabase by lazy { WorkoutRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { WorkoutRepository(workoutDatabase.workoutDao()) }
}