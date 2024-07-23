package com.example.filmer.data

import android.content.Context
import android.content.SharedPreferences
import com.example.filmer.BuildConfig
import javax.inject.Inject

class PreferenceProvider @Inject constructor(context: Context) {

    private val appContext = context.applicationContext

    private val preferences: SharedPreferences =
        appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    init {
        if (preferences.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preferences.edit().apply {
                putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY)
                putBoolean(KEY_FIRST_LAUNCH, false)
                apply()
            }
        }
    }

    fun saveDefaultCategory(category: String) {
        preferences.edit().apply {
            putString(KEY_DEFAULT_CATEGORY, category)
            apply()
        }
    }

    fun getDefaultCategory(): String {
        return preferences.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    fun getAppTheme(): String {
        return preferences.getString(APP_THEME_KEY, APP_THEME_DARK) ?: APP_THEME_DARK
    }

    fun saveAppTheme(theme: String) {
        preferences.edit().apply {
            putString(APP_THEME_KEY, theme)
            apply()
        }
    }

    private var trialCacheTime: Long? = null
    var trialState: Boolean? = null
        private set
        get() {
            if (System.currentTimeMillis() - (trialCacheTime ?: 0) > 500 || field == null) {
                field = checkTrialState()
                trialCacheTime = System.currentTimeMillis()
            }
            return field!!
        }


    private fun checkTrialState(): Boolean {
        return if (BuildConfig.FLAVOR == "paid") true
        else {
            val timer = preferences.getLong(trialStartKey, 0L)
            if (timer == 0L) {
                val trialStart = System.currentTimeMillis()
                preferences.edit().putLong(trialStartKey, trialStart).apply()
                true
            } else {
                System.currentTimeMillis() - timer < trialDuration
            }
        }
    }


    fun saveStartPosterData(
        filmId: Long,
        viewCount: Long,
        startViewCount: Long,
        percentage: Float
    ) {
        preferences.edit().apply {
            putLong(filmStartPosterID, filmId)
            putLong(filmStartPosterCount, viewCount)
            putLong(filmStartPosterStartCount, startViewCount)
            putFloat(filmStartPosterPercentage, percentage)
            apply()
        }
    }

    fun getStartPosterID(): Long = preferences.getLong(filmStartPosterID, -1)
    fun getStartPosterCount(): Long = preferences.getLong(filmStartPosterCount, 0)
    fun getStartPosterPercentage(): Float = preferences.getFloat(filmStartPosterPercentage, 0f)
    fun getStartPosterStartCount(): Long = preferences.getLong(filmStartPosterStartCount, 0)

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"

        private const val APP_THEME_KEY = "app_theme_key"
        const val APP_THEME_LIGHT = "app_theme_light"
        const val APP_THEME_DARK = "app_theme_dark"


        const val trialDuration = 1000 * 60 * 60 * 24 * 7  // 7 days
        const val trialStartKey = "trial_start_key"


        const val filmStartPosterID = "filmStartPosterId"
        const val filmStartPosterCount = "filmStartPosterCount"
        const val filmStartPosterStartCount = "filmStartPosterStartCount"
        const val filmStartPosterPercentage = "filmStartPosterPercentage"
    }
}