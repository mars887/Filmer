package com.example.filmer.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.filmer.R
import com.example.filmer.views.fragments.SelectionsFragment
import com.example.filmer.views.fragments.TVFragment
import com.example.filmer.views.fragments.WatchLaterFragment
import com.example.filmer.data.FilmData
import com.example.filmer.databinding.ActivityMainBinding
import com.example.filmer.views.fragments.FilmDetailsFragment
import com.example.filmer.views.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isDetailsOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
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
                        if ((fragment as TVFragment).countOfFavorites() > 0) {
                            fragment.onlyFavorites = true
                        } else {
                            Toast.makeText(this, "no favorites!", Toast.LENGTH_SHORT).show()
                            return@setOnItemSelectedListener false
                        }
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
}