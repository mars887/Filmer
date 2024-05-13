package com.example.filmer.views.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.filmer.R
import com.example.filmer.data.api.FilmApiConstants
import com.example.filmer.data.FilmData
import com.example.filmer.databinding.FragmentFilmDetailsBinding
import com.example.filmer.viewmodel.FilmDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FilmDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFilmDetailsBinding
    private val viewModel: FilmDetailsViewModel by viewModels()
    private lateinit var film: FilmData
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        film = arguments?.get("film") as FilmData

        binding.apply {

            Glide.with(root)
                .load(FilmApiConstants.IMAGES_URL + "w780" + film.poster)
                .centerCrop()
                .into(detailsPoster)

            detailsDescription.text = film.description
            detailsToolbar.title = film.title

            favoriteFab.setImageResource(if (film.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
            favoriteFab.setOnClickListener {
                film.isFavorite = !film.isFavorite
                favoriteFab.setImageResource(if (film.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
            }

            shareFab.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this film: ${film.title} \n\n ${film.description}"
                )
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share To:"))
            }

            val fabSize = favoriteFab.customSize
            val fabMargin = (detailsToolbar.minimumHeight - fabSize) / 2.0
            favoriteFab.translationY = (-fabMargin).toFloat()
            favoriteFab.translationX = (-fabMargin).toFloat()

            shareFab.translationY = (-fabMargin).toFloat()
            shareFab.translationX = (-fabMargin * 2 - fabSize).toFloat()

            detailsFabDownloadWp.translationY = (-fabMargin).toFloat()
            detailsFabDownloadWp.translationX = (-fabMargin * 3 - fabSize * 2).toFloat()

            detailsFabDownloadWp.setOnClickListener {
                performAsyncLoadOfPoster()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val contentValues = ContentValues().apply {

                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    film.title.handleSingleQuote()
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.DATE_ADDED,
                    System.currentTimeMillis() / 1000
                )
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmerPosters")
            }

            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            val outputStream = contentResolver.openOutputStream(uri!!)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)

            outputStream.close()
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film.title.handleSingleQuote(),
                film.description.handleSingleQuote()
            )
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster() {
        if (!checkPermission()) {
            requestPermission()
            return
        }

        MainScope().launch {
            binding.progressBar.isVisible = true

            val job = scope.async {
                viewModel.loadWallpaper(FilmApiConstants.IMAGES_URL + "original" + film.poster)
            }

            saveToGallery(job.await())

            Snackbar.make(
                binding.root,
                R.string.downloaded_to_gallery,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.open) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.type = "image/*"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .show()

            binding.progressBar.isVisible = false
        }
    }
}