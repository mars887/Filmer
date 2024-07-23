package com.example.filmer.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.filmer.App
import com.example.filmer.R
import com.example.filmer.databinding.FragmentReklamaBinding
import com.example.filmer.usecases.CheckPosterStateUseCase
import com.example.filmer.viewmodel.ReklamaFragmentViewModel
import com.example.filmer.views.MainActivity
import com.example.remote_module.entity.FilmApiConstants
import com.example.sql_module.FilmData
import javax.inject.Inject

class ReklamaFragment(private val film: FilmData) : Fragment() {
    private var _binding: FragmentReklamaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReklamaFragmentViewModel by viewModels<ReklamaFragmentViewModel> {
        ReklamaFragmentViewModel.Factory(film)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReklamaBinding.inflate(inflater, container, false)
        App.instance.appComponent.inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(binding.root)
            .load(FilmApiConstants.IMAGES_URL + "w780" + viewModel.film.poster)
            .centerCrop()
            .into(binding.posterImageView)

        binding.filmTitleTextView.text = viewModel.film.title
        binding.filmDescriptionTextView.text = viewModel.film.description
        binding.closeButton.setOnClickListener {
             MainActivity.checkInstance()?.closeCurrentFragment()
        }
        binding.posterImageView.setOnClickListener {
            MainActivity.checkInstance()?.launchDetailsFragment(film)
        }
        binding.filmTitleTextView.setOnClickListener {
            MainActivity.checkInstance()?.launchDetailsFragment(film)
        }
        binding.filmDescriptionTextView.setOnClickListener {
            MainActivity.checkInstance()?.launchDetailsFragment(film)
        }
    }
}