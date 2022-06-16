package com.chiu.reptime.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Workout::class), version = 2, exportSchema = false)
abstract class WorkoutRoomDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {

        private class WorkoutDatabaseCallback(
            private val scope: CoroutineScope
            ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.workoutDao())
                    }
                }
            }

            /**
             *  Populates database with two pre-defined workouts.
             */
            suspend fun populateDatabase(workoutDao: WorkoutDao) {
                workoutDao.deleteWorkouts()

                var repTimerWorkOne: RepTimer = RepTimer(11,11,11)
                var restTimerWorkOne: RestTimer = RestTimer(1,1)

                var workoutOne: Workout = Workout(
                    0,repTimerWorkOne, restTimerWorkOne, 5, "Workout One"
                )

                var repTimerWorkTwo: RepTimer = RepTimer(22, 22, 22)
                var restTimerWorkTwo: RestTimer = RestTimer(2,2)

                var workoutTwo: Workout = Workout(
                    0, repTimerWorkTwo, restTimerWorkTwo, 10, "Workout Two"
                )

                workoutDao.insert(workoutOne)
                workoutDao.insert(workoutTwo)
            }
        }

        /**
         *  INSTANCE will keep reference to the database whenever it is created.
         *  This will help maintain a single instance of the database open.
         *  Value of a volatile variable will never be cached. All writes and reads
         *  will be done to and from main memory. This helps ensure the value of
         *  INSTANCE is always up-to-date and the same for all execution threads.
         *  Changes by one thread to INSTANCE are visible to all other threads
         *  immediately.
         */
        @Volatile
        private var INSTANCE: WorkoutRoomDatabase? = null

        /**
         * Gets the instance for the database if created or creates the database.
         * To ensure that only one thread of execution can enter block of code,
         * wrap the code to get the database inside a synchronized block so that
         * it helps eliminate race condition where multiple threads ask for a
         * database instance at the same time.
         *
         * @param context will be needed by the database builder
         *
         * @return the database as a [WorkoutRoomDatabase]
         */
        fun getDatabase(context: Context, scope: CoroutineScope): WorkoutRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val  instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutRoomDatabase::class.java,
                    "workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(WorkoutDatabaseCallback(scope))
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}