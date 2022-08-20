package com.chiu.reptime.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chiu.reptime.R
import com.chiu.reptime.WorkoutApplication
import com.chiu.reptime.WorkoutViewModel
import com.chiu.reptime.constants
import com.chiu.reptime.databinding.FragmentStartWorkoutBinding
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer

class StartWorkoutFragment : Fragment() {

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.WorkoutViewModelFactory(
            (activity?.application as WorkoutApplication).repository
        )
    }

    private var _binding: FragmentStartWorkoutBinding? = null
    private val binding get() = _binding!!

    // counter to be used in the rep and rest timers
    private var timeLeft: Long = 0

    // the total time the rep counter will run for
    private var repTotalTime: Long = 0
    // the total time the rest counter will run for
    private var restTotalTime: Long = 0

    private var repCountDownTimer: CountDownTimer? = null
    private var restCountDownTimer: CountDownTimer? = null

    private lateinit var repTimer: RepTimer
    private lateinit var restTimer: RestTimer
    private var totalReps: Int = 0
    private var workoutName: String = ""

    private var inRepState = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentStartWorkoutBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences(getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE) ?: return

        val repHour = sharedPreferences.getInt(getString(R.string.rep_timer_hour_key), 0)
        val repMinute = sharedPreferences.getInt(getString(R.string.rep_timer_minute_key), 0)
        val repSecond = sharedPreferences.getInt(getString(R.string.rep_timer_second_key), 0)

        val restMinute = sharedPreferences.getInt(getString(R.string.rest_timer_minute_key), 0)
        val restSecond = sharedPreferences.getInt(getString(R.string.rest_timer_second_key), 0)

        repTimer = RepTimer(repHour, repMinute, repSecond)
        restTimer = RestTimer(restMinute, restSecond)

        updateRepAndRestTime()

        totalReps = sharedPreferences.getInt(getString(R.string.number_reps_key), 0)
        workoutName = sharedPreferences.getString(getString(R.string.workout_name_key), "") ?: ""

        binding.nameLabel.text = workoutName
        binding.remainingRepsLabel.text = totalReps.toString()
        binding.timerLabel.text = formatTimerDisplay((repTotalTime/ constants.MILLISECONS_IN_SECOND) - 1)

        binding.pbTimer.progress = constants.PROGRESS_BAR_FULL

        startWorkoutListeners(sharedPreferences)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        repCountDownTimer?.cancel()
        restCountDownTimer?.cancel()
    }

    /**
     * Starts a counter for each repetition.
     *
     * @param totalTime the total time the repetition is to go on for
     * @param numReps the total number of repetitions
     */
    private fun startRepCounter(totalTime: Long, numReps: Int, sharedPreferences: SharedPreferences) {
        cancelTimers()
        inRepState = true
        timeLeft = totalTime

        binding.pbTimer.progress = constants.PROGRESS_BAR_FULL
        binding.remainingRepsLabel.text = numReps.toString()

        repCountDownTimer = object : CountDownTimer(totalTime, constants.MILLISECONS_IN_SECOND.toLong()) {

            override fun onTick(millisUntilFinished: Long) {
                // TODO: Have [ProgressBar] change progress based on time elapsed.
                binding.timerLabel.text = formatTimerDisplay((timeLeft/ constants.MILLISECONS_IN_SECOND) - 1)
                timeLeft -= constants.MILLISECONS_IN_SECOND

                with(sharedPreferences.edit()) {
                    putLong(getString(R.string.remaining_rep_timer_key), timeLeft)
                    apply()
                }

                /*
                 *  This will ensure that once the timer hits '0', the label that updates
                 *  the number of reps is synchronized with the timer.
                 */
                /*if (timeLeft == 0L && numReps > 0) {
                    binding.remainingRepsLabel.text = (numReps - 1).toString()
                    with(sharedPreferences.edit()) {
                        putInt(getString(R.string.current_number_reps_key), numReps - 1)
                        apply()
                    }
                }*/
            }

            override fun onFinish() {
                /*  Continue workout as long as more repetitions remain:
                 *      if user has selected a rest time then:
                 *          start the rest timer
                 *      else:
                 *         start another rep timer
                 */
                binding.remainingRepsLabel.text = (numReps - 1).toString()
                with(sharedPreferences.edit()) {
                    putInt(getString(R.string.current_number_reps_key), numReps - 1)
                    apply()
                }
                if (numReps > 1) {
                    /*  Check to see when no rest time is set:
                     *    no rest time given means jump straight into the
                     *    next repetition
                     */
                    if (restTotalTime > 0L) {
                        startRestTimer(restTotalTime, numReps - 1, sharedPreferences)
                    } else {
                        startRepCounter(repTotalTime, numReps - 1, sharedPreferences)
                    }

                } else {
                    onWorkoutComplete(sharedPreferences)
                }
            }
        }.start()
    }

    /**
     * Starts a counter for the amount of time to rest between repetition.
     *
     * @param totalTime the total amount of time to rest between repetition
     * @param repsRemaining the total number of repetitions remaining
     */
    private fun startRestTimer(
        totalTime: Long,
        repsRemaining: Int,
        sharedPreferences: SharedPreferences
    ) {
        cancelTimers()
        inRepState = false

        binding.pbTimer.progress = constants.PROGRESS_BAR_EMPTY

        timeLeft = totalTime

        restCountDownTimer = object : CountDownTimer(totalTime, constants.MILLISECONS_IN_SECOND.toLong()) {

            override fun onTick(millisUntilFinished: Long) {
                binding.timerLabel.text = formatTimerDisplay((timeLeft/ constants.MILLISECONS_IN_SECOND) - 1)
                timeLeft -= constants.MILLISECONS_IN_SECOND

                with (sharedPreferences.edit()) {
                    putLong(getString(R.string.remaining_rest_timer_key), timeLeft)
                    apply()
                }
            }

            override fun onFinish() {
                if (repsRemaining >= 1) {
                    startRepCounter(repTotalTime, repsRemaining, sharedPreferences)
                }
            }
        }.start()

    }

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

    /**
     *  Events to happen after a workout is completed, such as
     *  changing UI display, updating history, etc.
     */
    private fun onWorkoutComplete(sharedPreferences: SharedPreferences) {
        binding.timerLabel.text = "Done"

        // reset the count for the current number of reps for next iteration
        with (sharedPreferences.edit()) {
            putInt(getString(R.string.current_number_reps_key), totalReps)
            apply()
        }

        // only allow reset
        binding.pauseBtn.visibility = View.INVISIBLE
        binding.resumeBtn.visibility = View.INVISIBLE
    }

    /**
     *   Obtain the total time for each repetition and the rest time in between repetitions
     *   from the passed in values from [CreateWorkoutFragment].
     */
    private fun updateRepAndRestTime() {
        repTotalTime = (repTimeToSeconds(repTimer) * constants.MILLISECONS_IN_SECOND) + constants.MILLISECONS_IN_SECOND
        restTotalTime = (restTimeToSeconds(restTimer) * constants.MILLISECONS_IN_SECOND) + constants.MILLISECONS_IN_SECOND
    }

    /**
     *  Starts 'Listeners' for START, PAUSE, RESET, and RESUME buttons.
     */
    private fun startWorkoutListeners(sharedPreferences: SharedPreferences) {

        binding.startBtn.setOnClickListener {
            if (totalReps > 0) {
                startRepCounter(repTotalTime, totalReps, sharedPreferences)
                it.visibility = View.INVISIBLE
                binding.resumeBtn.visibility = View.INVISIBLE
                binding.resetBtn.visibility = View.VISIBLE
                binding.pauseBtn.visibility = View.VISIBLE
            }
        }

        binding.resetBtn.setOnClickListener {
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.INVISIBLE
            binding.resumeBtn.visibility = View.INVISIBLE
            it.visibility = View.INVISIBLE

            cancelTimers()

            binding.remainingRepsLabel.text = sharedPreferences.getInt(getString(R.string.number_reps_key), 0).toString()
            binding.timerLabel.text = formatTimerDisplay((repTotalTime / constants.MILLISECONS_IN_SECOND) - 1)
            binding.pbTimer.progress = constants.PROGRESS_BAR_FULL

            with (sharedPreferences.edit()) {
                putInt(getString(R.string.current_number_reps_key), totalReps)
                putLong(getString(R.string.remaining_rest_timer_key), restTotalTime)
                putLong(getString(R.string.remaining_rep_timer_key), repTotalTime)
                apply()
            }
        }

        binding.pauseBtn.setOnClickListener {
            // cancel for now until implement pause feature
            cancelTimers()

            //Log.v("ONPAUSE", sharedPreferences.getInt(getString(R.string.current_number_reps_key), 0).toString())

            it.visibility = View.INVISIBLE
            binding.startBtn.visibility = View.INVISIBLE
            binding.resetBtn.visibility = View.VISIBLE
            binding.resumeBtn.visibility = View.VISIBLE
        }

        binding.resumeBtn.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.startBtn.visibility = View.INVISIBLE
            binding.resetBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.VISIBLE

            val currentNumReps = sharedPreferences.getInt(getString(R.string.current_number_reps_key), totalReps)

            //Log.v("ONRESUME", sharedPreferences.getInt(getString(R.string.current_number_reps_key), 0).toString())

            if (inRepState) {
                timeLeft = sharedPreferences.getLong(getString(R.string.remaining_rep_timer_key), 0)
                startRepCounter(timeLeft, currentNumReps, sharedPreferences)
            } else {
                timeLeft = sharedPreferences.getLong(getString(R.string.remaining_rest_timer_key), 0)
                startRestTimer(timeLeft, currentNumReps, sharedPreferences)
            }
        }
    }

    private fun cancelTimers() {
        repCountDownTimer?.cancel()
        restCountDownTimer?.cancel()
    }

    private fun saveCurrentRepTimerToSharedPref(
                 repTime: Long,
                 sharedPreferences: SharedPreferences) {
        with(sharedPreferences.edit()) {
            putLong(getString(R.string.remaining_rep_timer_key), 0)
            apply()
        }
    }

    private fun saveCurrentRestTimerToSharedPref(
        restTime: Long,
        sharedPreferences: SharedPreferences)  {
        with(sharedPreferences.edit()) {
            putLong(getString(R.string.remaining_rest_timer_key), 0)
            apply()
        }
    }



}