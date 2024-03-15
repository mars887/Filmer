package com.example.filmer

import android.app.Application
import com.example.filmer.data.api.FilmApi
import com.example.filmer.data.api.FilmApiConstants
import com.example.filmer.data.FilmDataBase
import com.example.filmer.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class App : Application() {

    lateinit var filmDataBase: FilmDataBase
    lateinit var interactor: Interactor

    override fun onCreate() {
        super.onCreate()
        instance = this

        val okHttpClient = OkHttpClient.Builder()
            //Настраиваем таймауты для медленного интернета
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            //Добавляем логгер
            .addInterceptor(HttpLoggingInterceptor().apply {

                    level = HttpLoggingInterceptor.Level.BASIC

            })
            .build()

        val retrofit = Retrofit.Builder()
            //Указываем базовый URL из констант
            .baseUrl(FilmApiConstants.BASE_URL)
            //Добавляем конвертер
            .addConverterFactory(GsonConverterFactory.create())
            //Добавляем кастомный клиент
            .client(okHttpClient)
            .build()

        val filmsService = retrofit.create(FilmApi::class.java)

        filmDataBase = FilmDataBase()

        interactor = Interactor(filmDataBase, filmsService)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}