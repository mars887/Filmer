package com.example.filmer.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FilmDetailsViewModel : ViewModel() {

    suspend fun loadWallpaper(url: String): Bitmap {
        return suspendCoroutine {
            val bitmap = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
            it.resume(bitmap)
        }
    }

}