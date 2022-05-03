package com.chiu.reptime.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.chiu.reptime.constants
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

    // counter to be used in the rep and rest timers
    private var timeLeft: Long = 0

    // the total time the rep counter will run for
    private var repTotalTime: Long = 0
    // the total time the rest counter will run for
    private var restTotalTime: Long = 0

    private var repCountDownTimer: CountDownTimer? = null
    private var restCountDownTimer: CountDownTimer? = null

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

        repTotalTime = (repTimeToSeconds(repTimer) * constants.MILLISECONS_IN_SECOND) + constants.MILLISECONS_IN_SECOND
        restTotalTime = (restTimeToSeconds(restTimer) * constants.MILLISECONS_IN_SECOND) + constants.MILLISECONS_IN_SECOND

        binding.pbTimer.progress = constants.PROGRESS_BAR_FULL

        if (numberReps > 0) {
            startRepCounter(repTotalTime, numberReps)
        } else {
            defaultDisplay()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Starts a counter for each repetition.
     *
     * @param totalTime the total time the repetition is to go on for
     * @param numReps the total number of repetitions
     */
    private fun startRepCounter(totalTime: Long, numReps: Int) {
        repCountDownTimer?.cancel()

        binding.remainingRepsLabel.text = numReps.toString()

        timeLeft = totalTime

        repCountDownTimer = object : CountDownTimer(totalTime, constants.MILLISECONS_IN_SECOND.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                // TODO: Have [ProgressBar] change progress based on time elapsed.
                binding.timerLabel.text = formatTimerDisplay((timeLeft/constants.MILLISECONS_IN_SECOND) - 1)
                timeLeft -= constants.MILLISECONS_IN_SECOND

                /*
                 *  This will ensure that once the timer hits '0', the label that updates
                 *  the number of reps is synchronized with the timer.
                 */
                if (timeLeft == 0L && numReps > 0) {
                    binding.remainingRepsLabel.text = (numReps - 1).toString()
                }
            }

            override fun onFinish() {
                // when the repCounter is done, must check to see
                // how many more reps are left to do:
                //    if there are more reps to do, start the rest timer
                //    else stop the timer

                if (numReps > 1) {
                    /* TODO: when there are still reps to do, then start
                     *        a rest timer before continuing to do another
                     *          rep.
                     */
                    startRepCounter(repTotalTime, numReps - 1)
                }
            }
        }.start()
    }

    /*private fun startRestTimer() {

        restCountDownTimer?.cancel()

        restCountDownTimer = object : CountDownTimer(0, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                TODO("Not yet implemented")
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }

        }.start()
    }*/

    /**
     * Converts the hour(s), minute(s), seconds(s) in [RepTimer] to seconds.
     *
     * @param repTimer a timer holding hour(s), minute(s), and second(s)
     * @return the total number of seconds
     */
    private fun repTimeToSeconds(repTimer: RepTimer): Long {
        val repHour: Long = repTimer.hour.toLong()
        val repMinute: Long = repTimer.minute.toLong()
        val repSecond: Long = repTimer.second.toLong()

        return (repHour * constants.MINUTES_IN_HOUR * constants.SECONDS_IN_MINUTE) +
                (repMinute * constants.SECONDS_IN_MINUTE) + repSecond
    }

    /**
     * Converts the minute(s) and second(s) in [RestTimer] to seconds.
     *
     * @param restTimer timer holding minute(s) and second(s)
     * @return the total number of seconds
     */
    private fun restTimeToSeconds(restTimer: RestTimer): Long {
        val restMinute: Long = restTimer.minute.toLong()
        val restSecond: Long = restTimer.second.toLong()

        return (restMinute * constants.SECONDS_IN_MINUTE) + restSecond
    }

    /**
     * Converts the number of seconds to a string in the format of:
     *      "hh:mm:ss"
     *
     * @param time the number of seconds
     * @return "hh:mm:ss"
     */
    private fun formatTimerDisplay(time: Long): String {
        val hour = time / 3600
        val minutes = (time % 3600) / 60
        val seconds = (time % 3600) % 60

        if (time < constants.SECONDS_IN_MINUTE)
            return "$seconds"

        if (time < constants.SECONDS_IN_MINUTE * constants.MINUTES_IN_HOUR)
            return "$minutes:$seconds"

        return "$hour:$minutes:$seconds"
    }

    /**
     * Sets the display to 'zero'. This will be the default in the
     * cases when there is no valid workout to perform.
     */
    private fun defaultDisplay() {
        binding.remainingRepsLabel.text = "0"
        binding.timerLabel.text = "0"
        binding.pbTimer.progress = 0
    }
}