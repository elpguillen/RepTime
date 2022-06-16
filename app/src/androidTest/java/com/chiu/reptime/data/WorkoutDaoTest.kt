package com.chiu.reptime.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.chiu.reptime.*
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

    // Set up before each test
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        workoutDatabase = Room.inMemoryDatabaseBuilder(context, WorkoutRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        workoutDao = workoutDatabase.workoutDao()
        workouts = ArrayList()
    }

    // Actions to take after each test
    @After
    fun closeDb() {
        workoutDatabase.close()
        workouts.clear()
    }

    /**
     *  Test to check that 'inserts' and reads via 'getWorkout' work as intended.
     */
    @Test
    fun writeAndReadWorkout() = runBlocking {
        // Test data from DataUtil to use for this testcase
        val testTimer: Triple<Int, Int, Int> = timerTimesList[0]
        val numberReps: Int = numberRepsList[2]
        val workoutName: String = nameList[0]

        val workout = createWorkout(testTimer.first, testTimer.second, testTimer.third,
                                        numberReps, workoutName, 1)
        workoutDao.insert(workout)

        val workoutRetrieved: Workout = workoutDao.getWorkout(workoutName).asLiveData().getOrAwaitValue()

        // Test to make sure that all values from the created/inserted Workout
        // match the values from the Workout retrieved.
        TestCase.assertTrue(
            workoutMismatchOutputMessage(workout, workoutRetrieved),
            workoutsAreEqual(workout, workoutRetrieved)
        )
    }

    /**
     *  Checks to make sure that 'getNumberWorkouts' retrieves the total number of
     *  [Workout]s that have been inserted into the database.
     */
    @Test
    fun countWorkouts() = runBlocking {
        // Each test starts with a new Database so it should be empty
        var numWorkouts: Int = workoutDao.getNumberWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "The database should have been empty.",
            0,
            numWorkouts
        )

        createUniqueWorkouts(workouts)

        // add one workout and test that the right number of workouts
        // is returned by 'getNumberWorkouts'
        workoutDao.insert(workouts[0])
        numWorkouts = workoutDao.getNumberWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "There should be one workout in the database.",
            1,
            numWorkouts
        )

        // test to check 'getNumberWorkouts' returns no workouts after
        // database has been deleted.
        workoutDao.deleteWorkouts()
        numWorkouts = workoutDao.getNumberWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "Database should be empty after clearing it.",
            0,
            numWorkouts
        )

        // insert all the created and workouts and test that 'getNumberWorkouts'
        // returns a value equal to the number of items in workout arraylist
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

    /**
     *  Checks whether 'getAllWorkouts' retrieves all the [Workout]s
     *  inserted into the database.
     */
    @Test
    fun retrieveAllWorkouts() = runBlocking {

        // Test to make sure Database is empty
        val allWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        assert(allWorkouts.isEmpty()) { "The database should have been empty." }

        // Test to make sure all items are inserted and that
        // "getAllWorkouts" retrieves all the workouts from the database
        createUniqueWorkouts(workouts)

        for (item in 0 until workouts.size) {
            workoutDao.insert(workouts[item])
        }

        val retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "The number of items inserted does not match items retrieved.",
            workouts.size,
            retrievedWorkouts.size)
    }

    /**
     *  Checks to see if 'deleteWorkouts' removes all data in the database.
     */
    @Test
    fun clearDatabase() = runBlocking {

        // The database should be empty at beginning of test.
        var retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.isEmpty()) { "Database should have been empty." }

        createUniqueWorkouts(workouts)

        for (item in 0 until workouts.size) {
            workoutDao.insert(workouts[item])
        }

        // Test to make sure workouts are being inserted into database
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        TestCase.assertEquals(
            "The number of items inserted does not match items retrieved.",
            workouts.size,
            retrievedWorkouts.size
        )

        workoutDao.deleteWorkouts()

        val retrieveAfterDelete = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrieveAfterDelete.isEmpty()) { "Not all items were deleted." }

        // Try deleting database when there is nothing in it
        workoutDao.deleteWorkouts()
        val retrieveAfterDeleteOnEmpty = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrieveAfterDeleteOnEmpty.isEmpty()) { "The database should be empty." }
    }

    /**
     *  Test for inserting specific [Workout] via 'getWorkout(name)'
     */
    @Test
    fun insertWorkout() = runBlocking {

        val workoutOneDataIndex = 1
        val workoutOne = createWorkout(
            timerTimesList[workoutOneDataIndex].first, timerTimesList[workoutOneDataIndex].second,
            timerTimesList[workoutOneDataIndex].third, numberRepsList[workoutOneDataIndex],
            nameList[workoutOneDataIndex], workoutOneDataIndex)

        workoutDao.insert(workoutOne)

        var retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "There should only be one item in database at this point.",
            1,
            retrievedWorkouts.size
        )

        val workoutTwoDataIndex = 2
        val workoutTwo = createWorkout(
            timerTimesList[workoutTwoDataIndex].first, timerTimesList[workoutTwoDataIndex].second,
            timerTimesList[workoutTwoDataIndex].third, numberRepsList[workoutTwoDataIndex],
            nameList[workoutTwoDataIndex], workoutTwoDataIndex
        )

        workoutDao.insert(workoutTwo)

        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "There should only be two items in the database at this point.",
            2,
            retrievedWorkouts.size
        )

        val retrievedWorkoutOne = workoutDao.getWorkout(nameList[workoutOneDataIndex]).asLiveData().getOrAwaitValue()
        val retrievedWorkoutTwo = workoutDao.getWorkout(nameList[workoutTwoDataIndex]).asLiveData().getOrAwaitValue()
        val retrieveNonExistantWorkout = workoutDao.getWorkout("does not exist").asLiveData().getOrAwaitValue()

        // Case: Workout trying to retrieve is not in the database
        TestCase.assertNull(
            "should return null since Workout that was attempted to retrieve is not in database",
            retrieveNonExistantWorkout)

        // Case: Workout inserted into database should match with Workout
        //          retrieved with same name from the database.
        TestCase.assertTrue(
            "Workouts do not match.",
            workoutsAreEqual(workoutOne, retrievedWorkoutOne)
        )

        // Case: Workout inserted into database should match with Workout
        //          retrieved with same name from the database.
        TestCase.assertTrue(
            "Workouts do not match",
            workoutsAreEqual(workoutTwo, retrievedWorkoutTwo)
        )

        // Case: Two different retrieved Workouts do not match. Each Workout should
        //          be unique on id and name.
        TestCase.assertFalse(
            "Workouts should not have matched.",
            workoutsAreEqual(retrievedWorkoutOne, retrievedWorkoutTwo)
        )
    }

    /**
     *  Checks to see if we can insert [Workout]s and retrieve them all via 'getAllWorkouts'
     *  and makes sure that the inserted [Workout]s match the retrieved [Workout]s.
     */
    @Test
    fun insertAndRetrieveAllItems() = runBlocking {

        createUniqueWorkouts(workouts)

        for (workoutIndex in 0 until workouts.size) {
            workoutDao.insert(workouts[workoutIndex])
        }

        val retrievedWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        TestCase.assertEquals(
            "The number of workouts inserted does not match the workouts retrieved from the database.",
            workouts.size,
            retrievedWorkouts.size
        )
        // add each workout to map, key will be the name of the workout
        val workoutMap: Map<String, Workout> =
            retrievedWorkouts.associateBy( {it.name}, {it} )

        // check that each item in workouts is found in the workouts retrieved
        // from the database
        for (index in 0 until workouts.size) {

            assert(workoutMap.containsKey(workouts[index].name))
            val workout: Workout? = workoutMap[workouts[index].name]

            // Checks that [Workout] inserted matches [Workout] retrieved.
            TestCase.assertTrue(
                workoutMismatchOutputMessage(workouts[index], workout!!),
                workoutsAreEqual(workouts[index], workout)
            )
        }
    }

    /**
     *  Checks that 'getAlphabetizedWorkouts' retrieves all [Workout]s within
     *  the database in ASCENDING order by name.
     */
    @Test
    fun insertAndAlphabetized() = runBlocking {

        createUniqueWorkouts(workouts)             // workouts added are not sorted in any way

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        // get the sorted (ASCENDING by name) list of workouts with 'getAlphabetizedWorkouts
        val retrievedWorkouts: List<Workout> = workoutDao.getAlphabetizedWorkouts().asLiveData().getOrAwaitValue()
        // sort the workouts list that was created and inserted into the database
        workouts.sortBy { it.name }

        assert(retrievedWorkouts.size == workouts.size)
        // check that retrievedWorkouts is sorted in ASCENDING order (by name) by
        // comparing it to a sorted (by name) version of workouts.
        for (index in 0 until workouts.size) {
            TestCase.assertTrue(
                workoutMismatchOutputMessage(workouts[index], retrievedWorkouts[index]),
                workoutsAreEqual(workouts[index], retrievedWorkouts[index])
            )
        }
    }

    /**
     *  Checks that 'delete([Workout])' deletes the given [Workout] from the [Workout] database
     *  and that 'deleteWorkouts' deletes all items from the [Workout] database.
     */
    @Test
    fun deleteWorkouts() = runBlocking {

        createUniqueWorkouts(workouts)

        // There should be no workouts in the database at this point
        var retrievedWorkouts: List<Workout> = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.isEmpty())

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()

        // since the data has all unique names/ids, all workouts should
        // have been inserted into the database
        assert(retrievedWorkouts.size == workouts.size)

        // Begin test to remove a specific [Workout] from the [Workout] database:
        //  get a pseudo random item in the database and delete it
        val indexToRemove: Int= (0..workouts.size-1).random() // generates random int from 0 to workouts.size-1 included
        val workoutToDelete: Workout = workouts[indexToRemove]

        // delete the workout chosen pseudo-randomly
        workoutDao.delete(workoutToDelete)

        // if only one workout was removed than size of retrievedWorkouts should be
        // one less than the size of the workouts list
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.size == (workouts.size - 1))

        // make sure that the deleted item was the item that was actually removed
        val workoutsMap: Map<String, Workout> = retrievedWorkouts.associateBy( {it.name}, {it} )
        val containsWorkoutToDelete: Boolean = workoutsMap.containsKey(workoutToDelete.name)
        TestCase.assertEquals(
            "workoutsMap should not contain deleted item",
            false,
            containsWorkoutToDelete
        )

        // Begin test to delete all workouts:
        workoutDao.deleteWorkouts()
        // let's make sure there are no workouts in the [Workout] database after calling 'deleteWorkouts'
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.isEmpty())

        // now let's try to delete previously deleted workout on an empty database
        workoutDao.delete(workoutToDelete)
        retrievedWorkouts = workoutDao.getAllWorkouts().asLiveData().getOrAwaitValue()
        assert(retrievedWorkouts.isEmpty())
    }

    /**
     *  Checks that [Workout]s inserted are unique by name.
     */
    @Test
    fun insertingSameName() = runBlocking {

        createUniqueWorkouts(workouts)

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

        // Case: insert item with same name but different id (should not insert)
        val workoutToCopy: Workout = workouts[0]
        val workoutWithSameName =
            Workout(
                workouts.size * 2,
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
    }

    /**
     *  Checks that [Workout]s inserted are unique by id.
     */
    @Test
    fun insertSameId() = runBlocking {

        createUniqueWorkouts(workouts)

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        var retrievedWorkouts: List<Workout> =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        TestCase.assertEquals(
            "Number of inserted workouts does not match number of retrieved workouts.",
            workouts.size,
            retrievedWorkouts.size
        )

        // Case: insert items with same id and name (should not insert)
        workoutDao.insert(workouts[0])

        retrievedWorkouts =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        TestCase.assertEquals(
            "Number of valid insertions into the database does not match number of retrieved workouts",
            workouts.size,
            retrievedWorkouts.size
        )

        for (index in 0 until workouts.size) {
            workoutDao.insert(workouts[index])
        }

        retrievedWorkouts =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        TestCase.assertEquals(
            "Number of valid insertions into the database does not match number of retrieved workouts",
            workouts.size,
            retrievedWorkouts.size
        )

        // Case: insert item with same id but different name (should not insert)
        val workoutSameIdDiffName =
            Workout(
                workouts[1].id,
                workouts[1].repTimer,
                workouts[1].restTimer,
                workouts[1].numberReps,
                "This workout name has not yet been used."
            )

        workoutDao.insert(workoutSameIdDiffName)

        retrievedWorkouts =
            workoutDao.getAllWorkouts()
                .asLiveData()
                .getOrAwaitValue()

        TestCase.assertEquals(
            "Workout with same id was inserted.",
            workouts.size,
            retrievedWorkouts.size
        )
    }
}