package com.chiu.reptime.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao){

    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkouts()
    val alphabetizedWorkouts: Flow<List<Workout>> = workoutDao.getAlphabetizedWorkouts()

    @WorkerThread
    suspend fun insert(workout: Workout) {
        workoutDao.insert(workout)
    }

    @WorkerThread
    suspend fun update(workout: Workout) {
        workoutDao.update(workout)
    }

    @WorkerThread
    suspend fun delete(workout: Workout) {
        workoutDao.delete(workout)
    }

    @WorkerThread
    suspend fun deleteAllWorkouts() {
        workoutDao.deleteWorkouts()
    }

    fun getWorkoutByName(name: String): Flow<Workout> = workoutDao.getWorkout(name)
}