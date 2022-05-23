package com.chiu.reptime

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.chiu.reptime.data.Workout
import com.chiu.reptime.data.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val allWorkouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()
    val alphabetizedWorkouts: LiveData<List<Workout>> = repository.alphabetizedWorkouts.asLiveData()

    fun insert(workout: Workout) = viewModelScope.launch {
        repository.insert(workout)
    }

    fun update(workout: Workout) = viewModelScope.launch {
        repository.update(workout)
    }

    fun delete(workout: Workout) = viewModelScope.launch {
        repository.delete(workout)
    }

    fun deleteWorkouts() = viewModelScope.launch {
        repository.deleteAllWorkouts()
    }

    class WorkoutViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WorkoutViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}