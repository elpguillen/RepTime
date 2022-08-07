package com.chiu.reptime.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chiu.reptime.R
import com.chiu.reptime.databinding.FragmentCreateWorkoutBinding
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.chiu.reptime.WorkoutApplication
import com.chiu.reptime.WorkoutViewModel
import com.chiu.reptime.data.Workout
import com.chiu.reptime.models.RepTimer
import com.chiu.reptime.models.RestTimer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.NumberFormatException

/**
 * Fragment that will be used to help create workouts.
 */
class CreateWorkoutFragment : Fragment() {

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.WorkoutViewModelFactory(
            (activity?.application as WorkoutApplication).repository
        )
    }

    private var _binding: FragmentCreateWorkoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_create_workout, container, false)
        _binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTimerMaxMinValues()

        // get instance of SharedPreferences to save [NumberPicker] values
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.shared_preference_file_key),
            Context.MODE_PRIVATE) ?: return

        restoreCreateWorkoutState(sharedPref)
        startCreateWorkoutListeners(sharedPref)

        viewModel.allWorkouts.observe(this.viewLifecycleOwner) {
            // do something here
        }
    }

    /**
     *  Creates the range of values for each NumberPicker in [CreateWorkoutFragment].
     */
    private fun setTimerMaxMinValues() {
        // set hour rep timer max and min values
        binding.hourRepTimerNp.maxValue = 23
        binding.hourRepTimerNp.minValue = 0

        // set minute rep timer max and min values
        binding.minuteRepTimerNp.maxValue = 59
        binding.minuteRepTimerNp.minValue = 0

        // set second rep timer max and min values
        binding.secondRepTimerNp.maxValue = 59
        binding.secondRepTimerNp.minValue = 0

        // set minute rest timer max and min values
        binding.minuteRestTimerNp.maxValue = 59
        binding.minuteRestTimerNp.minValue = 0

        // set second rest timer max and min values
        binding.secondRestTimerNp.maxValue = 59
        binding.secondRestTimerNp.minValue = 0

    }

    /**
     * Sets up [NumberPicker.setOnValueChangedListener] for each [NumberPicker] in
     * [CreateWorkoutFragment]. Values are stored in [SharedPreferences] when
     * there is a change in the value in any [NumberPicker].
     *
     * @param sharedPreferences the instance of [SharedPreferences] to save into
     */
    private fun setUpPickerChangeListeners(sharedPreferences: SharedPreferences) {

        binding.hourRepTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.rep_timer_hour_key), newVal)
                apply()
            }
        }

        binding.minuteRepTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.rep_timer_minute_key), newVal)
                apply()
            }
        }

        binding.secondRepTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.rep_timer_second_key), newVal)
                apply()
            }
        }

        binding.minuteRestTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.rest_timer_minute_key), newVal)
                apply()
            }
        }

        binding.secondRestTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.rest_timer_second_key), newVal)
                apply()
            }
        }

    }

    /**
     *  Sets up [TextView.addTextChangedListener] for [EditText] responsible for holding
     *  the name of the workout. Values are stored in [SharedPreferences] when
     *  there is a change to the workout name.
     *
     *  @param sharedPreferences the instance of [SharedPreferences] to save into
     */
    private fun setUpNameInputChangeListeners(sharedPreferences: SharedPreferences) {

        binding.workoutNameInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                with(sharedPreferences.edit()) {
                    val workoutName: String = s?.toString() ?: ""
                    putString(getString(R.string.workout_name_key), workoutName)
                    apply()
                }
            }
        })
    }

    /**
     *  Sets up [TextView.addTextChangedListener] for [EditText] responsible for holding
     *  the number of repetitions for the workout. Values are stored in [SharedPreferences]
     *  when there is a change to the number of repetitions.
     *
     *  @param sharedPreferences the instance of [SharedPreferences] to save into
     */
    private fun setUpNumberRepsChangeListener(sharedPreferences: SharedPreferences) {

        binding.numberRepInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                with(sharedPreferences.edit()) {
                    val stringReps: String = s?.toString() ?: "1"
                    var numberReps: Int

                    try {
                        numberReps = stringReps.toInt()
                    } catch (nfe: NumberFormatException) {
                        numberReps = 1
                    }
                    //val numberReps2: Int = s?.toString()?.toInt() ?: 0
                    putInt(getString(R.string.number_reps_key), numberReps)
                    apply()
                }
            }
        })
    }

    /**
     *  Starts 'Listeners' for when user changes rest time, repetition time,
     *  name of the workout, the number of repetitions. As well as when the
     *  user clicks on the 'Start' and 'Save' buttons.
     *
     *  @param sharedPreferences the instance of [SharedPreferences] to save into
     */
    private fun startCreateWorkoutListeners(sharedPreferences: SharedPreferences) {

        // start listening for any value change for each [NumberPicker]
        setUpPickerChangeListeners(sharedPreferences)
        // start listening for any change in the name of the workout
        setUpNameInputChangeListeners(sharedPreferences)
        // start listening for any change in the number of repetitions
        setUpNumberRepsChangeListener(sharedPreferences)

        // start an onClickListener for when user presses Save Workout Button
        binding.saveWorkoutBtn.setOnClickListener {
            // save workout to Room database
            onSaveWorkout()
        }
    }

    /**
     * Restores the previous values for each [NumberPicker]. Sets to '0' if
     * there is no previous value.
     *
     * @param sharedPreferences the instance of [SharedPreferences] to retrieve values from
     */
    private fun restorePickerValues(sharedPreferences: SharedPreferences) {

        // Restore values corresponding to the RepTimer
        binding.hourRepTimerNp.value = sharedPreferences.getInt(getString(R.string.rep_timer_hour_key), 0)
        binding.minuteRepTimerNp.value = sharedPreferences.getInt(getString(R.string.rep_timer_minute_key), 0)
        binding.secondRepTimerNp.value = sharedPreferences.getInt(getString(R.string.rep_timer_second_key), 0)

        // Restore values corresponding to the RestTimer
        binding.minuteRestTimerNp.value = sharedPreferences.getInt(getString(R.string.rest_timer_minute_key), 0)
        binding.secondRestTimerNp.value = sharedPreferences.getInt(getString(R.string.rest_timer_second_key), 0)
    }

    /**
     *  Restores the previous value entered into the Workout Name [EditText].
     *  Sets to empty string if there is no previous value.
     *
     *  @param sharedPreferences the instance of [SharedPreferences] to retrieve values from
     */
    private fun restoreWorkoutName(sharedPreferences: SharedPreferences) {
        val workoutEditable: Editable = Editable.Factory.getInstance().newEditable(
            sharedPreferences.getString(getString(R.string.workout_name_key), "")
        )

        binding.workoutNameInput.text = workoutEditable
    }

    /**
     *  Restores the previous value entered into the Number of Reps [EditText].
     *  Sets to '1' if there is no previous value.
     *
     *  @param sharedPreferences the instance of [SharedPreferences] to retrieve values from
     */
    private fun restoreNumberReps(sharedPreferences: SharedPreferences) {

        // since there is a hint, '0' default might be removed
        val numberReps: Int = sharedPreferences.getInt(getString(R.string.number_reps_key), 1)
        val numberRepsEditable: Editable = Editable.Factory.getInstance().newEditable(
            numberReps.toString()
        )

        binding.numberRepInput.text = numberRepsEditable
    }

    /**
     *  Restores the state of the Rest Timer, Repetition Timer, workout name,
     *  and number of repetitions.
     *
     *  @param sharedPreferences the instance of [SharedPreferences] to save into
     */
    private fun restoreCreateWorkoutState(sharedPreferences: SharedPreferences) {
        // retrieve the previous state of each [NumberPicker]
        restorePickerValues(sharedPreferences)
        // retrieve previous workout name
        restoreWorkoutName(sharedPreferences)
        // retrieve the previous state of repetitions
        restoreNumberReps(sharedPreferences)
    }

    /**
     *  Alert message to show in cases when there is no input for the
     *  name of the workout.
     */
    private fun showSaveWorkoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("ALERT!")
            .setMessage("Are you sure you want to save?")
            .setCancelable(false)
            .setNeutralButton("CANCEL") {_, _ ->}
            .setNegativeButton("NO") {_, _ ->
                //Log.d("HOUR REP TIME: ", binding.hourRepTimerNp.value.toString())
                //Log.d("SECOND REST TIME: ", binding.secondRestTimerNp.value.toString())
                //Log.d("NUMBER REPS: ", binding.numberRepInput.text.toString())
            }
            .setPositiveButton("YES") {_, _ ->
                // save workout to database
                // id???
                val workoutId = 0
                // rep timer
                val repHour: Int = binding.hourRepTimerNp.value
                val repMinute: Int = binding.minuteRepTimerNp.value
                val repSecond: Int = binding.secondRepTimerNp.value
                val repTimer = RepTimer(repHour, repMinute, repSecond)
                // rest timer
                val restMinute: Int = binding.minuteRestTimerNp.value
                val restSecond: Int = binding.secondRestTimerNp.value
                val restTimer = RestTimer(restMinute, restSecond)
                // number reps
                val numberReps: Int = binding.numberRepInput.text.toString().toInt()
                // name
                val workoutName: String = binding.workoutNameInput.text.toString().trim()
                // create workout from current parameters
                var currentWorkout: Workout =
                    Workout(
                        workoutId,
                        repTimer,
                        restTimer,
                        numberReps,
                        workoutName
                    )

                viewModel.insert(currentWorkout)
            }
            .show()
    }

    /**
     *  Action to take when the 'Save' button is clicked.
     */
    private fun onSaveWorkout() {
            showSaveWorkoutConfirmation()
    }

    private fun getRepTimer(): RepTimer{
        val hour: Int = binding.hourRepTimerNp.value
        val minute: Int = binding.minuteRepTimerNp.value
        val second: Int = binding.secondRepTimerNp.value

        return RepTimer(hour, minute, second)
    }

    private fun getRestTimer(): RestTimer {
        val minute: Int = binding.minuteRestTimerNp.value
        val second: Int = binding.secondRestTimerNp.value

        return RestTimer(minute, second)
    }

}