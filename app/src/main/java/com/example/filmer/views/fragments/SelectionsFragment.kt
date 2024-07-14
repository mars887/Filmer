package com.example.filmer.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.filmer.App
import com.example.filmer.databinding.FragmentSelectionsBinding
import com.example.filmer.util.AnimationHelper


class SelectionsFragment : Fragment() {
    private lateinit var binding: FragmentSelectionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectionsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(binding.selectionsFragmentRoot, requireActivity(), 4)
    }
}