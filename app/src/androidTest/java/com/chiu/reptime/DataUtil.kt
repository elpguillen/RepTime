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

fun createWorkouts(workouts: ArrayList<Workout>) {

    val minSize: Int = minOf(timerTimesList.size, numberRepsList.size, nameList.size)

    for (index in 0 until minSize) {

        val hour: Int = timerTimesList[index].first
        val minute: Int = timerTimesList[index].second
        val second: Int = timerTimesList[index].third

        val workout = Workout(
            index + 1, // basically works as auto-increment
            RepTimer(hour, minute, second),
            RestTimer(minute, second),
            numberRepsList[index],
            nameList[index]
        )
        workouts.add(workout)
    }
}