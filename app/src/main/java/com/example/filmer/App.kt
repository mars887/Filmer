package com.example.filmer

import android.app.Application
import java.util.stream.Collectors
import java.util.stream.Stream

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this


        libraryData = generateMediaList(10)
    }

    fun getRandomRData(): RData {
        val posterTitles = listOf(
            "cheto film", "drygoe kino", "pochti kak kino", "vrode eto kino",
            "kak kino no da", "tipo nazvanie", "tozhe cheto"
        )
        return RData(
            getRandomPosterId(),
            posterTitles.random(),
            "poster description\n To connect to your server, copy the server address and enter it in your Minecraft client, as a new server or with \"Direct Connect\". You can find the server address on the server page.",
            false,
            (10..95).random()
        )
    }

    private fun generateMediaList(count: Int): List<RData> {
        return Stream.generate {
            getRandomRData()
        }.limit(count.toLong()).collect(Collectors.toList())
    }

    fun getRandomPosterId(): Int {
        return when ((1..5).random()) {
            1 -> R.drawable.poster1
            2 -> R.drawable.poster2
            3 -> R.drawable.poster3
            4 -> R.drawable.poster4
            5 -> R.drawable.poster5
            else -> R.drawable.poster1
        }
    }

    companion object {
        lateinit var instance: App
            private set
        lateinit var libraryData: List<RData>
    }
}