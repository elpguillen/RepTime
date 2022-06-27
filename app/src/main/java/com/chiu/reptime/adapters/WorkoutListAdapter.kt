package com.chiu.reptime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chiu.reptime.data.Workout
import com.chiu.reptime.databinding.WorkoutListItemBinding

class WorkoutListAdapter(private val onItemClicked: () -> Unit) :
    ListAdapter<Workout, WorkoutListAdapter.WorkoutViewHolder>(DiffCallback) {

    /**
     *  ViewHolder for [Workout]
     *
     *  @param binding  data binding for 'workout_list_item'
     */
    class WorkoutViewHolder(private var binding: WorkoutListItemBinding):
                RecyclerView.ViewHolder(binding.root) {
                    fun  bind(workout: Workout) {
                        val repHour: Int = workout.repTimer.hour
                        val repMinute: Int = workout.repTimer.minute
                        val repSecond: Int = workout.repTimer.second
                        val repTime = "$repHour:$repMinute:$repSecond"

                        val restMinute: Int = workout.restTimer.minute
                        val restSecond: Int = workout.restTimer.second
                        val restTime = "$restMinute:$restSecond"

                        binding.nameLabel.text = workout.name
                        binding.repsLabel.text = workout.numberReps.toString()
                        binding.repTimeLabel.text = repTime
                        binding.restTimeLabel.text = restTime
                    }
                }

    /**
     *  Will be used by adapter to compute diffs between lists.
     */
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Workout>() {
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     *  Creates ViewHolder and inflates view.
     *
     *  @param parent ViewGroup that view will be added to once bound to adapter position
     *  @param viewType integer representing the type of view being created
     *
     *  @return WorkoutViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val workoutViewHolder : WorkoutViewHolder =
            WorkoutViewHolder(
                WorkoutListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        workoutViewHolder.itemView.setOnClickListener {
            // do something here when item is clicked
        }

        return workoutViewHolder
    }

    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder    ViewHolder representing item at given position to bind the data to
     * @param position  position of item within the adapter
     */
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}