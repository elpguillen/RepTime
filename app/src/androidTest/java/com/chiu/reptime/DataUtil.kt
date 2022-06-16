package com.chiu.reptime

import com.chiu.reptime.data.Workout
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer

val timerTimesList: List<Triple<Int, Int, Int>> =
    mutableListOf(
        Triple(0,0,0), Triple(1,1,1), Triple(2,2,2),
        Triple(3,3,3), Triple(4,4,4), Triple(5,5,5))

val numberRepsList: List<Int> = mutableListOf(0,1,2,3,4,5)
val nameList: List<String> = mutableListOf("Zero", "One", "Two", "Three", "Four", "Five")

/**
 *  Creates unique [Workout]s to add to a workout ArrayList.
 *
 *  @param workouts the ArrayList to add the workouts to
 */
fun createUniqueWorkouts(workouts: ArrayList<Workout>) {

    // the workouts we are trying to insert should be unique by name
    workouts.distinctBy { it.name }

    val minSize: Int = minOf(timerTimesList.size, numberRepsList.size, nameList.size)

    for (index in 0 until minSize) {

        val hour: Int = timerTimesList[index].first
        val minute: Int = timerTimesList[index].second
        val second: Int = timerTimesList[index].third

        val workout = createWorkout(
            hour, minute, second,
            numberRepsList[index],
            nameList[index],
            index + 1
        )

        workouts.add(workout)
    }
}

/**
 *  Creates a brand new [Workout] instance.
 *
 *  @param hour         the number of hours each repetition will last
 *  @param minute       the number of minutes each repetition and rest period will last
 *  @param second       the number of seconds each repetition and rest period will last
 *  @param numberReps   the number of repetitions in the [Workout]
 *  @param name         the name given to the [Workout]
 *  @param id           the integer id for the [Workout]
 *
 *  @return  a [Workout] instance created from the passed in parameters
 */
fun createWorkout(hour: Int, minute: Int, second: Int, numberReps: Int, name: String, id: Int): Workout {
    return Workout(
        id,
        RepTimer(hour, minute,second),
        RestTimer(minute, second),
        numberReps,
        name
    )
}

/**
 *  Compares two [Workout]s. Checks to see if the passed in values for each
 *  parameter match.
 *
 *  @param workoutOne  one of the [Workout]s to compare with
 *  @param workoutTwo  one of the [Workout]s to compare with
 *
 *  @return  a Boolean value, true if [Workout]'s parameters match and false otherwise
 */
fun workoutsAreEqual(workoutOne: Workout?, workoutTwo: Workout?): Boolean {
    val workoutsMatch: Boolean = true

    if (workoutOne == null || workoutTwo == null)
        return !workoutsMatch

    // check that the values of each attribute for both workouts match
    if ( (workoutOne.id != workoutTwo.id) || (workoutOne.name != workoutTwo.name) ||
        (workoutOne.numberReps != workoutTwo.numberReps) ||
        (!repTimersMatch(workoutOne.repTimer, workoutTwo.repTimer)) ||
        (!restTimerMatch(workoutOne.restTimer, workoutTwo.restTimer)) )
        return !workoutsMatch

    return workoutsMatch
}

/**
 *  Determines whether or not two [RepTimer]s match.
 *
 *  @param repTimerOne  one of the [RepTimer]s
 *  @param repTimerTwo  one of the [RepTimer]s
 *
 *  @return a Boolean; true if [RepTimer]s match and false otherwise
 */
fun repTimersMatch(repTimerOne: RepTimer, repTimerTwo: RepTimer): Boolean {
    if ( (repTimerOne.hour != repTimerTwo.hour) ||
            (repTimerOne.minute != repTimerTwo.minute) ||
                (repTimerOne.second != repTimerTwo.second) )
        return false
    return true
}

/**
 * Determines whether or not two [RestTimer]s match.
 *
 * @param restTimerOne one of the [RestTimer]s
 * @param restTimerTwo one of the [RestTimer]s
 *
 * @return a Boolean; true if [RestTimer]s match and false otherwise
 */
fun restTimerMatch(restTimerOne: RestTimer, restTimerTwo: RestTimer): Boolean {
    if ((restTimerOne.minute != restTimerTwo.minute) ||
        (restTimerOne.second != restTimerTwo.second) )
        return false
    return true
}

/**
 *  Output message for when the parameter values for two [Workout]s being compared do not match.
 *  Will display the parameters for each [Workout] to help determine where they do not match.
 *
 *  @param workoutOne a [Workout] to be used for comparison
 *  @param workoutTwo a [Workout] to be used for comparison
 */
fun workoutMismatchOutputMessage(workoutOne: Workout, workoutTwo: Workout): String {

    return "The given Workouts did not match:\n" +
            "Workout One: <id: ${workoutOne.id}>, <numberReps: ${workoutOne.name}>, <name: ${workoutOne.name}>, " +
            "<RepHour: ${workoutOne.repTimer.hour}>, <RepMinute: ${workoutOne.repTimer.minute}>, <RepSecond: ${workoutOne.repTimer.second}>, " +
            "<RestMinute: ${workoutOne.restTimer.minute}>, <RestSecond: ${workoutOne.restTimer.second}> \n" +
            "Workout Two: <id: ${workoutTwo.id}>, <numberReps: ${workoutTwo.name}>, <name: ${workoutTwo.name}>, " +
            "<RepHour: ${workoutTwo.repTimer.hour}>, <RepMinute: ${workoutTwo.repTimer.minute}>, <RepSecond: ${workoutTwo.repTimer.second}>, " +
            "<RestMinute: ${workoutTwo.restTimer.minute}>, <RestSecond: ${workoutTwo.restTimer.second}>"
}