package com.example.filmer.views.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.filmer.App
import com.example.filmer.R
import com.example.filmer.data.db.SQLInteractor
import com.example.filmer.databinding.FragmentFilmDetailsBinding
import com.example.filmer.viewmodel.FilmDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilmDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFilmDetailsBinding
    private val viewModel: FilmDetailsViewModel by viewModels()
    private lateinit var film: com.example.sql_module.FilmData
    private val scope = CoroutineScope(Dispatchers.IO)
    @Inject lateinit var sqlInteractor: SQLInteractor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.appComponent.inject(this)

        film = arguments?.get("film") as com.example.sql_module.FilmData

        binding.apply {

            Glide.with(root)
                .load(com.example.remote_module.entity.FilmApiConstants.IMAGES_URL + "w780" + film.poster)
                .centerCrop()
                .into(detailsPoster)

            detailsDescription.text = film.description
            detailsToolbar.title = film.title

            val fabSize = popupMenuFab.customSize
            val fabMargin = (detailsToolbar.minimumHeight - fabSize) / 2.0

            // -------------------------------- favorite fab

            favoriteFab.translationY = (-fabMargin).toFloat()
            favoriteFab.translationX = (-fabMargin * 2 - fabSize).toFloat()

            favoriteFab.setImageDrawable(AppCompatResources.getDrawable(requireContext(),if (film.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon))
            favoriteFab.setOnClickListener {
                film.isFavorite = !film.isFavorite
                favoriteFab.setImageDrawable(AppCompatResources.getDrawable(requireContext(),if (film.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon))
                if (film.isFavorite) sqlInteractor.addToFavorites(film)
                else sqlInteractor.removeFromFavorites(film)
            }

            // -------------------------------- popup menu fab

            popupMenuFab.translationY = (-fabMargin).toFloat()
            popupMenuFab.translationX = (-fabMargin).toFloat()

            popupMenuFab.setOnClickListener {
                showPopUpMenu(it)
            }
        }
    }

    private fun showPopUpMenu(view: View) {
        val popUpMenu = PopupMenu(this.requireContext(), view)
        popUpMenu.inflate(R.menu.film_details_popup_menu)
        popUpMenu.setOnMenuItemClickListener { menuItem ->
            return@setOnMenuItemClickListener when (menuItem.itemId) {
                R.id.fdpu_download_poster -> {
                    performAsyncLoadOfPoster()
                    true
                }

                R.id.fdpu_share -> {
                    shareFabPressed()
                    true
                }

                R.id.fdpu_watch_later -> {
                    App.instance.notificationsHelper.sendWatchLater(film)
                    true
                }

                else -> false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popUpMenu.setForceShowIcon(true)
        }
        popUpMenu.show()
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
        val chandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("poster_loader",throwable.message?: "")
            throwable.printStackTrace()
        }

        scope.launch(chandler) {
            val job = scope.async {
                viewModel.loadWallpaper(com.example.remote_module.entity.FilmApiConstants.IMAGES_URL + "original" + film.poster)
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

        }
    }

    private fun shareFabPressed() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this film: ${film.title} \n\n ${film.description}"
        )
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }
}