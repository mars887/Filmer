package com.example.filmer.views

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.filmer.App
import com.example.filmer.R
import com.example.filmer.databinding.ActivityMainBinding
import com.example.filmer.domain.Interact
import com.example.filmer.isDarkThemeOn
import com.example.filmer.services.PowerStatusBroadcast
import com.example.filmer.usecases.CheckPosterStateUseCase
import com.example.filmer.views.fragments.FilmDetailsFragment
import com.example.filmer.views.fragments.ReklamaFragment
import com.example.filmer.views.fragments.SelectionsFragment
import com.example.filmer.views.fragments.SettingsFragment
import com.example.filmer.views.fragments.TVFragment
import com.example.filmer.views.fragments.WatchLaterFragment
import com.example.sql_module.FilmData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isDetailsOpen: Boolean = false
    private var darkBeenEnabled = false
    private lateinit var pwBroadcast: PowerStatusBroadcast

    @Inject
    lateinit var checkPosterStateUseCase: CheckPosterStateUseCase

    @Inject
    lateinit var interact: Interact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        instance = this

        App.instance.appComponent.inject(this)
        detailsFilmIntent = intent.getStringExtra("showDetailsTitle")

        startPosterCheck()

        initPowerBroadcast()

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

    private fun startPosterCheck() {
        val result = checkPosterStateUseCase()

        lifecycleScope.launch {
            result.collect {
                Log.d("TAG",it.toString())
                if(it != null) {
                    interact.getFilmById(it).collect { film ->
                        if(film == null) return@collect
                        val fragment = ReklamaFragment(film)
                        changeFragment(fragment,"reklama")
                    }
                }
            }
        }
    }

    private fun initPowerBroadcast() {
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
    }

    private fun bottomNavigationEvent(it: Int): Boolean {
        val hasTrial = App.instance.interactor.checkTrialState()
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
                if (!hasTrial) {
                    openBuyPairVersionSnakbar()
                    false
                } else {
                    val tag = "tv"
                    val fragment = checkFragmentExistence(tag)
                    if (fragment != null) {
                        (fragment as TVFragment).onlyFavorites = true
                    }

                    changeFragment(fragment ?: TVFragment(true), tag)
                    true
                }

            }

            R.id.bottomMenu_watch_later -> {
                val tag = "watch_later"
                val fragment = checkFragmentExistence(tag)

                changeFragment(fragment ?: WatchLaterFragment(), tag)
                true
            }

            R.id.bottomMenu_selections -> {
                if (!hasTrial) {
                    openBuyPairVersionSnakbar()
                    false
                } else {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag)

                    changeFragment(fragment ?: SelectionsFragment(), tag)
                    true
                }
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

    private fun openBuyPairVersionSnakbar() {
        Snackbar.make(
            this,
            binding.root,
            "trial ended",
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(binding.bottomNavigation)
            .setAction("buy full version") {
                println("buying paid version")
            }.show()
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

    fun closeCurrentFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pwBroadcast)
    }

    companion object {
        fun checkInstance(): MainActivity? {
            return if (::instance.isInitialized) instance else null
        }

        var detailsFilmIntent: String? = null
        lateinit var instance: MainActivity
            private set
    }
}