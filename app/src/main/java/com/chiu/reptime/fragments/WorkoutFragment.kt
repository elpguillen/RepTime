package com.chiu.reptime.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.chiu.reptime.databinding.FragmentWorkoutBinding
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private val args: WorkoutFragmentArgs by navArgs()

    private var startTime = 10;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repTimer = args.repTimer
        val restTimer = args.restTimer
        val numberReps = args.numberOfReps

        setTimerAndReps(repTimer, restTimer, numberReps)

        startRepCounter()
    }

    private fun startRepCounter() {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerLabel.text = startTime.toString()
                startTime--
            }

            override fun onFinish() {
                // when the repCounter is done, must check to see
                // how many more reps are left to do:
                //    if there are more reps to do, start the rest timer
                //    else stop the timer
                binding.timerLabel.text = "finished"
            }
        }.start()
    }

    private fun formatRestTimer(restTimer: RestTimer): String {

        val minute: String = restTimer.minute.toString()
        val second: String = restTimer.second.toString()

        return "$minute m $second s"
    }

    private fun formatRepTimer(repTimer: RepTimer): String {
        val hour: String = repTimer.hour.toString()
        val minute: String = repTimer.minute.toString()
        val second: String = repTimer.second.toString()

        return "$hour h $minute m $second s"
    }

    private fun setTimerAndReps(
        repTimer: RepTimer,
        restTimer: RestTimer,
        numReps: Int) {
        binding.timePerRepLabel.text = formatRepTimer(repTimer)
        binding.restLabel.text = formatRestTimer(restTimer)
        binding.numberRepLabel.text = numReps.toString()
    }

}