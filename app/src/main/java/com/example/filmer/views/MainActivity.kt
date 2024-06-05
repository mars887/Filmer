package com.example.filmer.views

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.filmer.R
import com.example.filmer.databinding.ActivityMainBinding
import com.example.filmer.isDarkThemeOn
import com.example.filmer.services.PowerStatusBroadcast
import com.example.filmer.views.fragments.FilmDetailsFragment
import com.example.filmer.views.fragments.SelectionsFragment
import com.example.filmer.views.fragments.SettingsFragment
import com.example.filmer.views.fragments.TVFragment
import com.example.filmer.views.fragments.WatchLaterFragment
import com.example.sql_module.FilmData
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isDetailsOpen: Boolean = false
    private var darkBeenEnabled = false
    private lateinit var pwBroadcast: PowerStatusBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pwBroadcast = PowerStatusBroadcast { action, state ->
            when (action) {
                Intent.ACTION_POWER_CONNECTED -> {

                        Snackbar.make(
                            this,
                            binding.root,
                            "charger connected",
                            Snackbar.LENGTH_LONG
                        )
                            .setAnchorView(binding.bottomNavigation)
                            .setAction("light mode?") {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                recreate()
                                darkBeenEnabled = false
                            }.show()

                }

                Intent.ACTION_BATTERY_LOW -> {
                    if (!this.isDarkThemeOn()) {
                        Snackbar.make(
                            this,
                            binding.root,
                            "battery is low",
                            Snackbar.LENGTH_LONG
                        )
                            .setAnchorView(binding.bottomNavigation)
                            .setAction("dark mode?") {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                recreate()
                                darkBeenEnabled = true
                            }.show()
                    }
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)
        registerReceiver(pwBroadcast, filter)

        bottomNavigationEvent(R.id.bottomMenu_tv)
        binding.bottomNavigation.setOnItemSelectedListener {
            bottomNavigationEvent(it.itemId)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    isDetailsOpen -> {
                        isDetailsOpen = false
                        supportFragmentManager.popBackStack()
                    }

                    binding.bottomNavigation.selectedItemId != R.id.bottomMenu_tv -> {
                        binding.bottomNavigation.selectedItemId = R.id.bottomMenu_tv
                    }

                    else -> {
                        finish()
                    }
                }
            }
        })

        binding.bottomNavigation.selectedItemId = R.id.bottomMenu_tv

    }

    private fun bottomNavigationEvent(it: Int): Boolean {
        return when (it) {
            R.id.bottomMenu_tv -> {
                val tag = "tv"
                val fragment = checkFragmentExistence(tag)
                if (fragment != null) {
                    (fragment as TVFragment).onlyFavorites = false
                }

                changeFragment(fragment ?: TVFragment(false), tag)
                true
            }

            R.id.bottomMenu_favorites -> {
                val tag = "tv"
                val fragment = checkFragmentExistence(tag)
                if (fragment != null) {
                    (fragment as TVFragment).onlyFavorites = true
                }

                changeFragment(fragment ?: TVFragment(true), tag)
                true
            }

            R.id.bottomMenu_watch_later -> {
                val tag = "watch_later"
                val fragment = checkFragmentExistence(tag)

                changeFragment(fragment ?: WatchLaterFragment(), tag)
                true
            }

            R.id.bottomMenu_selections -> {
                val tag = "selections"
                val fragment = checkFragmentExistence(tag)

                changeFragment(fragment ?: SelectionsFragment(), tag)
                true
            }

            R.id.bottomMenu_settings -> {
                val tag = "settings"
                val fragment = checkFragmentExistence(tag)
                changeFragment(fragment ?: SettingsFragment(), tag)
                true
            }

            else -> {
                false
            }
        }
    }

    fun getCurrentBackground(): Int {
        val currFragment = supportFragmentManager.fragments.lastOrNull()
        return when (currFragment?.tag) {
            "tv" -> {
                R.drawable.fragment_tv_back_shape
            }

            "watch_later" -> {
                R.drawable.fragment_watch_later_back_shape
            }

            "selections" -> {
                R.drawable.fragment_selections_back_shape
            }

            else -> {
                R.drawable.main_activity_def_back_shape
            }
        }
    }

    fun launchDetailsFragment(film: FilmData) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)
        val fragment = FilmDetailsFragment()
        fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack("details")
            .commit()
        isDetailsOpen = true
    }

    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        binding.mainFragmentContainer.setBackgroundResource(getCurrentBackground())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pwBroadcast)
    }
}