package com.chiu.reptime.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.chiu.reptime.R
import com.chiu.reptime.WorkoutApplication
import com.chiu.reptime.WorkoutViewModel
import com.chiu.reptime.databinding.FragmentHistoryBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.WorkoutViewModelFactory(
            (activity?.application as WorkoutApplication).repository
        )
    }

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_history, container, false)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
}