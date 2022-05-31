package com.chiu.reptime.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.chiu.reptime.*
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WorkoutDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var workoutDatabase: WorkoutRoomDatabase
    private lateinit var workoutDao: WorkoutDao

    private lateinit var workouts: ArrayList<Workout>

    // sets up before each test
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        workoutDatabase = Room.inMemoryDatabaseBuilder(context, WorkoutRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        workoutDao = workoutDatabase.workoutDao()

        // make sure that a new set of workouts is created for each test
        workouts = ArrayList()
    }

    // actions to take after each test
    @After
    fun closeDb() {
        workoutDatabase.close()
        workouts.clear()
    }

    // test to check if write and read from the database works correctly
    @Test
    fun writeAndReadWorkout() = runBlocking {
        val repTimerWorkOne: RepTimer = RepTimer(11,11,11)
        val restTimerWorkOne: RestTimer = RestTimer(1,1)
        val workout = Workout(0, repTimerWorkOne, restTimerWorkOne, 2, "Workout One")

        /*var repTimerWorkTwo: RepTimer = RepTimer(22, 22, 22)
        var restTimerWorkTwo: RestTimer = RestTimer(2,2)
        val workout2 = Workout(0, repTimerWorkTwo, restTimerWorkTwo, 5, "Workout Two")*/

        workoutDao.insert(workout)
        //workoutDao.insert(workout2)

        val firstWorkout: Workout = workoutDao.getWorkout("Workout One").asLiveData().getOrAwaitValue()

        TestCase.assertEquals("Write and Read to Workout Database did not succeeded.",
            firstWorkout.name,"Workout One")
    }

    @Test
    fun countWorkouts() = runBlocking {
        // Database should be empty at this point
        var numWorkouts: Int = workoutDao.getNumberWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "The database should have been empty.",
            0,
            numWorkouts
        )

        createWorkouts(workouts)

        // add one workout and test that the right number of workouts
        // is returned by 'getNumberWorkouts'
        workoutDao.insert(workouts[0])
        numWorkouts = workoutDao.getNumberWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "There should be one workout in the database.",
            1,
            numWorkouts
        )

        // insert all the created and workouts and test 'getNumberWorkouts'
        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        numWorkouts = workoutDao.getNumberWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "All the created workouts should be in the database.",
            workouts.size,
            numWorkouts
        )
    }

    // test to check if all workouts are retrieved via 'getAllWorkouts'
    @Test
    fun retrieveAllWorkouts() = runBlocking {

        // Test to make sure Database is empty
        val allWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals("The database should have been empty.", 0, allWorkouts.size)

        // Test to make sure all items are inserted and that
        // "getAllWorkouts" retrieves all the workouts from the database
        createWorkouts(workouts)

        for (item in 0 until workouts.size) {
            workoutDao.insert(workouts[item])
        }

        val retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "The number of items inserted does not match items retrieved.",
            minOf(timerTimesList.size, numberRepsList.size, nameList.size),
            retrievedWorkouts.size)
    }

    // test for checking database gets cleared via 'deleteWorkouts'
    @Test
    fun clearDatabase() = runBlocking {
        createWorkouts(workouts)

        for (item in 0 until workouts.size) {
            workoutDao.insert(workouts[item])
        }

        // Test to make sure workouts are being inserted into database
        val retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "The number of items inserted does not match items retrieved.",
            minOf(timerTimesList.size, numberRepsList.size, nameList.size),
            retrievedWorkouts.size
        )

        workoutDao.deleteWorkouts()

        val retrieveAfterDelete = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "Not all items were deleted",
            0,
            retrieveAfterDelete.size
        )

        //  lets try deleting database when there is nothing in it
        workoutDao.deleteWorkouts()
        val retrieveAfterDeleteOnEmpty = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "There should have been nothing to delete.",
            0,
            retrieveAfterDeleteOnEmpty.size
        )
    }

    // test for inserting specific workout via 'getWorkout(name)'
    @Test
    fun insertWorkout() = runBlocking {
        createWorkouts(workouts)

        workoutDao.insert(workouts[0])

        val firstWorkout = workoutDao.getWorkout("Zero").asLiveData().getOrAwaitValue()
        var retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "Only one item should have been inserted",
            1,
            retrievedWorkouts.size
        )

        TestCase.assertEquals(
            "Name of retrieved workout does not match the name of the workout that was inserted.",
            "Zero",
            firstWorkout.name
        )

        workoutDao.insert(workouts[1])

        val secondWorkout = workoutDao.getWorkout("One").asLiveData().getOrAwaitValue()
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "There should be two items inserted at this point.",
            2,
            retrievedWorkouts.size
        )

        TestCase.assertEquals(
            "Name of retrieved workout does not match the name of the workout that was inserted",
            "One",
            secondWorkout.name
        )

        // true if id's are not the same and false otherwise
        val notSameId = (firstWorkout.id != secondWorkout.id)
        Log.d("tag", "firstWorkout id is: ${firstWorkout.id}")
        Log.d("tag", "secondWorout id is: ${secondWorkout.id}")

        TestCase.assertEquals(
            "Should not have same id",
            true,
            notSameId)
    }

    @Test
    fun insertCount() = runBlocking {

        var retrievedWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        // The database should be empty at this point.
        TestCase.assertEquals(
            "There should be no items in the database.",
            0,
            retrievedWorkouts.size
        )

        createWorkouts(workouts)
        val mid = workouts.size / 2

        // insert only half of the items
        for (index in 0 until mid) {
            workoutDao.insert(workouts[index])
        }

        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "Number of items inserted at this point should be half of the items that were created using 'createWorkouts'",
            mid,
            retrievedWorkouts.size
        )

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        Log.d("INSERTCOUNT: ", "${workouts.size}")
        Log.d("INSERTCOUNT: ", "${retrievedWorkouts.size}")


        // Workout names should be unique, therefore when trying to insert the first half of the workouts
        // the database should ignore the insertion and just insert the second half
        TestCase.assertEquals(
            "Number of items at this point should be equal to the total number of items created using 'createWorkouts'",
            workouts.size,
            retrievedWorkouts.size
        )
    }

    @Test
    fun insertAndRetrieveAllItems() = runBlocking {
        createWorkouts(workouts)

        for (workoutIndex in 0 until workouts.size) {
            workoutDao.insert(workouts[workoutIndex])
        }

        val retrievedWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "The number of workouts does not match the workouts retrieved from the database.",
            workouts.size,
            retrievedWorkouts.size
        )

        // what if workouts.size != retrievedWorkouts.size???
        // when traversing list, you might encounter indexoutbounds
        // will happen if test does not pass, can try to get the min
        // of both and traverse that and test should still be kinda
        // valid.

        val workoutMap: Map<String, Workout> =
            retrievedWorkouts.associateBy( {it.name}, {it} )

        // check that each item in workouts is found in the workouts retrieved
        // from the database
        for (index in 0 until workouts.size) {
            assert(workoutMap.containsKey(workouts[index].name))

            val workout: Workout? = workoutMap[workouts[index].name]
            // continue test...

            // make sure workout == workouts[index]
            // repetition timers should match
            // rest timers should match
            // number of repetitions should match
            // name should match (something is completely wrong if at this point matching name fails)
            TestCase.assertEquals(
                "The repetition timer did not match!",
                workouts[index].repTimer,
                workout?.repTimer
            )

            TestCase.assertEquals(
                "The rest timer did not match!",
                workouts[index].restTimer,
                workout?.restTimer
            )

            TestCase.assertEquals(
                "The number of repetitions did not match!",
                workouts[index].numberReps,
                workout?.numberReps
            )

            TestCase.assertEquals(
                "The name of the workouts did not match!",
                workouts[index].name,
                workout?.name
            )

            // skip this test since database auto increments id, unless you want to create
            // data that specifically is made to help with this testcase
            /*TestCase.assertEquals(
                "The id of the workout does not match!",
                workouts[index].id,
                workout?.id
            )*/
        }
    }

    @Test
    fun insertAndAlphabetized() = runBlocking {
        createWorkouts(workouts)

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        // get the sorted list of workouts with 'getAlphabetizedWorkouts;
        val retrievedWorkouts: List<Workout> = workoutDao.getAlphabetizedWorkouts().asLiveData().getOrAwaitValue()
        // sort the workouts list that was created and inserted to database
        workouts.sortBy { it.name }

        assert(retrievedWorkouts.size == workouts.size)

        for (index in 0 until workouts.size) {
            TestCase.assertEquals(
                "The name of the workouts does not match at index: $index",
                workouts[index].name,
                retrievedWorkouts[index].name
            )
        }
    }

    @Test
    fun deleteWorkouts() = runBlocking {

        createWorkouts(workouts)

        var retrievedWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        // There should be no workouts in the database at this point
        assert(retrievedWorkouts.isEmpty())

        // insert workouts into the database
        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        // retrieve all workouts inserted into the database
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        // check id's of workouts inserted since was having issues with id matching
        // those of 'workouts', found out problem: each workout was being given an id
        // of 0, but since in the database they get incremented and default start is '1'
        // we needed to modify the creation of the workouts by auto-incrementing them
        // when they are created and giving same starting default of '1'
        /*for (index in 0 until retrievedWorkouts.size) {
            Log.d("WORKOUTSLIST", "${retrievedWorkouts[index].id}")
        }*/

        // since the data has all unique names/ids, all workouts should
        // have been inserted into the database
        assert(retrievedWorkouts.size == workouts.size)

        //lets get a pseudo random item in the database and delete it
        val indexToRemove: Int= (0..workouts.size-1).random() // generates random int from 0 to workouts.size-1 included
        val workoutToDelete: Workout = workouts[indexToRemove]

        //Log.d("DELETEWORKOUT", "${workoutToDelete.id}")
        //Log.d("DELETEWORKOUT", "${retrievedWorkouts[indexToRemove].id}")

        // delete the workout chosen pseudo-randomly
        workoutDao.delete(workoutToDelete)

        // check to see if correct workout was removed
        // if only one workout was removed than size of retrievedWorkouts should be one less than
        // the size of the workouts list
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        //Log.d("DELETEWORKOUT", "${retrievedWorkouts.size}")
        //Log.d("DELETEWORKOUT", "${workouts.size}")
        assert(retrievedWorkouts.size == (workouts.size - 1))

        // make sure that the deleted item is no longer in the database
        val workoutsMap: Map<String, Workout> = retrievedWorkouts.associateBy( {it.name}, {it} )

        val containsWorkoutToDelete: Boolean = workoutsMap.containsKey(workoutToDelete.name)
        TestCase.assertEquals(
            "workoutsMap should not contain deleted item",
            false,
            containsWorkoutToDelete
        )

        // lets delete all workouts
        workoutDao.deleteWorkouts()
        // let's make sure there are no workouts in the database
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.isEmpty())

        // now let's try to delete previous workout
        workoutDao.delete(workoutToDelete)
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.isEmpty())
    }

    @Test
    fun insertingSameName() = runBlocking {

        createWorkouts(workouts)

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        var retrievedWorkouts: List<Workout> =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        assert(workouts.size == retrievedWorkouts.size)

        // trying to insert a workout from workouts once again should fail:
        // should not be able to insert same workout since the name and id
        // will be the same as another workout
        workoutDao.insert(workouts[0])
        retrievedWorkouts =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        assert(workouts.size == retrievedWorkouts.size)

        // try to insert workout with the same name but different id
        val workoutToCopy: Workout = workouts[0]
        val workoutWithSameName: Workout =
            Workout(
                7,
                workoutToCopy.repTimer,
                workoutToCopy.restTimer,
                workoutToCopy.numberReps,
                workoutToCopy.name
            )
        workoutDao.insert(workoutWithSameName)

        retrievedWorkouts =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        TestCase.assertEquals(
            "No item should have been inserted, therefore size of 'retrievedWorkouts' should still be the same as 'workouts'",
            workouts.size,
            retrievedWorkouts.size
        )

        // try to insert a workout with same id but different workout name
        val workoutWithSameId: Workout =
            Workout(
                1,
                workoutToCopy.repTimer,
                workoutToCopy.restTimer,
                workoutToCopy.numberReps,
                "This workout name has not yet been used."
            )

        workoutDao.insert(workoutWithSameId)
        retrievedWorkouts =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        TestCase.assertEquals(
            "No item should have been inserted since workout has the same 'id' as a previous workout.",
            workouts.size,
            retrievedWorkouts.size
        )
    }

}