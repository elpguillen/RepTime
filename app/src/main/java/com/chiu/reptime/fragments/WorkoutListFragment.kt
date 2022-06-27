package com.chiu.reptime.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiu.reptime.R
import com.chiu.reptime.WorkoutApplication
import com.chiu.reptime.WorkoutViewModel
import com.chiu.reptime.adapters.WorkoutListAdapter
import com.chiu.reptime.data.Workout
import com.chiu.reptime.databinding.FragmentWorkoutListBinding
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutListFragment : Fragment() {

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.WorkoutViewModelFactory(
            (activity?.application as WorkoutApplication).repository
        )
    }

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_workout_list, container, false)
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: add action to take on item click
        val workoutAdapter = WorkoutListAdapter({})

        binding.workoutsList.layoutManager = LinearLayoutManager(this.context)
        binding.workoutsList.adapter = workoutAdapter

        //viewModel.deleteWorkouts()

        /*val workoutsList: ArrayList<Workout> = createWorkouts()

        for (workout in workoutsList) {
            // remove extra spaces end/beginning of string
            val newWorkout = Workout(0,workout.repTimer, workout.restTimer, workout.numberReps, workout.name.trim())
            viewModel.insert(workout)
        }*/

        viewModel.allWorkouts.observe(this.viewLifecycleOwner) {
            workouts -> workouts.let {
                workoutAdapter.submitList(it)
            }
        }
    }

    /**
     *  Creates unique workouts.
     *
     *  @return an ArrayList of unique workouts
     */
    private fun createWorkouts(): ArrayList<Workout> {

        val workoutList: ArrayList<Workout> = ArrayList()

        for (index in 0 until 35) {
            val repTime = RepTimer(index, index, index)
            val restTime = RestTimer(index,index)
            val id = 0
            val numReps = index + 1
            val name = "Workout $index"

            val workout = Workout(id, repTime, restTime, numReps, name)
            workoutList.add(workout)
        }

        return workoutList
    }
}