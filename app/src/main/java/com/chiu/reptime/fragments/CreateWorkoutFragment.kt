package com.chiu.reptime.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chiu.reptime.R
import com.chiu.reptime.databinding.FragmentCreateWorkoutBinding

/**
 * A simple [Fragment] subclass.
 * Use the [CreateWorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
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
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_create_workout, container, false)
        _binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }
}