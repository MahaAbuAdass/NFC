package com.example.nfcdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nfcdemo.databinding.MainScreenBinding

class HomeScreen : Fragment() {




    private lateinit var binding : MainScreenBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRead.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_readFragment)
        }
        binding.btnWrite.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_write)
        }
    }
}