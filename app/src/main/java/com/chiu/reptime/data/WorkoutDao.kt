package com.chiu.reptime.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    /**
     *  Inserts a [Workout] into the workoutDatabase.
     *
     *  @param  workout  the [Workout] to add into the workoutDatabase
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workout: Workout)

    /**
     *  Updates [Workout] in workoutDatabase.
     *
     *  @param  workout the [Workout] in workoutDatabase to update
     */
    @Update
    suspend fun update(workout: Workout)

    /**
     * Deletes the passed in [Workout] from the workoutDatabase.
     *
     * @param  workout  the [Workout] to delete from the workoutDatabase
     */
    @Delete
    suspend fun delete(workout: Workout)

    /**
     *  Deletes all items in the [Workout] table.
     */
    @Query("DELETE FROM workouts")
    suspend fun deleteWorkouts()

    /**
     *  Retrieve all items from the [Workout] table.
     *
     *  @return  all items in [Workout] table
     */
    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): Flow<List<Workout>>

    /**
     *  Retrieves all items from the [Workout] table in
     *  ascending order.
     *
     *  @return  all items in [Workout] table in ascending order
     */
    @Query("SELECT * FROM workouts ORDER BY name")
    fun getAlphabetizedWorkouts(): Flow<List<Workout>>

    /**
     *  Retrieves the workouts which match 'name' (case-sensitive)
     *
     *  @param  name  the name of the workout
     *  @return       the workout which matches 'name'
     */
    @Query("SELECT * FROM workouts WHERE name = :name")
    fun getWorkout(name: String): Flow<Workout>
}