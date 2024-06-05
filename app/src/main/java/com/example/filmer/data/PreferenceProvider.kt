package com.example.filmer.data

import android.content.Context
import android.content.SharedPreferences
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

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"

        private const val APP_THEME_KEY = "app_theme_key"
        public const val APP_THEME_LIGHT = "app_theme_light"
        public const val APP_THEME_DARK = "app_theme_dark"
    }
}