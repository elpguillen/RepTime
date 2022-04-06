package com.chiu.reptime.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chiu.reptime.R
import com.chiu.reptime.databinding.FragmentCreateWorkoutBinding
import android.widget.NumberPicker

/**
 * Fragment that will be used to help create workouts.
 *
 * TODO: will need to save state through something like SharedPreferences
 */
class CreateWorkoutFragment : Fragment() {

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

        // get SharedPreferences to save [NumberPicker] values
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.shared_preference_file_key),
            Context.MODE_PRIVATE) ?: return

        // retrieve the previous state of each [NumberPicker]
        restorePickerValues(sharedPref)
        // start listening for any value change for each [NumberPicker]
        setUpPickerChangeListeners(sharedPref)
    }

    /**
     * Creates the range of values for each NumberPicker in [CreateWorkoutFragment].
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
                putInt(getString(R.string.create_workout_hour_key), newVal)
                apply()
            }
        }

        binding.minuteRepTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.create_workout_minute_key), newVal)
                apply()
            }
        }

        binding.secondRepTimerNp.setOnValueChangedListener { picker, oldVal, newVal ->
            with(sharedPreferences.edit()) {
                putInt(getString(R.string.create_workout_second_key), newVal)
                apply()
            }
        }
    }

    /**
     * Restores the previous values for each [NumberPicker]. Sets to 0 if
     * there is no previous value.
     *
     * @param sharedPreferences the instance of [SharedPreferences] to save into
     */
    private fun restorePickerValues(sharedPreferences: SharedPreferences) {
        binding.hourRepTimerNp.value = sharedPreferences.getInt(getString(R.string.create_workout_hour_key), 0)
        binding.minuteRepTimerNp.value = sharedPreferences.getInt(getString(R.string.create_workout_minute_key), 0)
        binding.secondRepTimerNp.value = sharedPreferences.getInt(getString(R.string.create_workout_second_key), 0)
    }

}