package com.example.filmer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import com.example.filmer.databinding.ActivityMainBinding
import java.util.stream.Collectors
import java.util.stream.Stream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var filmList: ArrayList<RData>

    private var currentFragment: Int = VIDEOS_FRAGMENT
    private var isDetailsOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportFragmentManager.fragments.size == 0) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, HomeFragment())
                .commit()
            currentFragment = VIDEOS_FRAGMENT
        }

        filmList = getRandomList(10) as ArrayList<RData>

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (currentFragment == VIDEOS_FRAGMENT || currentFragment == FAVORITES_FRAGMENT) {
                    (supportFragmentManager.fragments.last() as HasSearch).onQueryTextSubmit(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (currentFragment == VIDEOS_FRAGMENT || currentFragment == FAVORITES_FRAGMENT) {
                    (supportFragmentManager.fragments.last() as HasSearch).onQueryTextChange(newText)
                }
                return false
            }

        })

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottomMenu_main -> {
                    if (currentFragment != VIDEOS_FRAGMENT || supportFragmentManager.backStackEntryCount == 0) {
                        supportFragmentManager.popBackStack()
                        currentFragment = VIDEOS_FRAGMENT
                        binding.searchView.visibility = View.VISIBLE
                        true
                    } else false
                }

                R.id.bottomMenu_favorites -> {
                    if (currentFragment != FAVORITES_FRAGMENT) {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.main_container, FavoritesFragment())
                            .addToBackStack("favorites")
                            .commit()
                        currentFragment = FAVORITES_FRAGMENT
                        binding.searchView.visibility = View.VISIBLE
                        true
                    } else false
                }

                R.id.bottomMenu_selections -> {
                    Toast.makeText(this, "selections", Toast.LENGTH_SHORT).show()
                    false
                }

                R.id.bottomMenu_watch_later -> {
                    Toast.makeText(this, "watch later", Toast.LENGTH_SHORT).show()
                    false
                }

                else -> {
                    false
                }
            }
        }
        binding.topAppBar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settings -> {
                        Toast.makeText(this@MainActivity, "settings", Toast.LENGTH_SHORT).show()
                    }

                    R.id.colorMode -> {
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                }
                true
            }
            setNavigationOnClickListener {
                Toast.makeText(this@MainActivity, "menu", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    isDetailsOpen -> {
                        isDetailsOpen = false
                        supportFragmentManager.popBackStack()
                    }

                    currentFragment != VIDEOS_FRAGMENT -> {
                        supportFragmentManager.popBackStack()
                        binding.bottomNavigation.setSelectedItemId(R.id.bottomMenu_main)
                        currentFragment = VIDEOS_FRAGMENT
                    }

                    else -> {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Вы хотите выйти?")
                            .setPositiveButton("Да") { _, _ ->
                                finish()
                            }
                            .setNegativeButton("Нет") { _, _ -> }
                            .show()
                    }

                }
            }
        })
    }

    fun launchDetailsFragment(film: RData) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)
        val fragment = FilmDetailsFragment()
        fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack("details")
            .commit()
        isDetailsOpen = true
    }

    fun getData(): ArrayList<RData> = filmList

    companion object {
        const val VIDEOS_FRAGMENT = 0
        const val FAVORITES_FRAGMENT = 1
        const val WATCH_LATER_FRAGMENT = 2
        const val LIBRARY_FRAGMENT = 3

        val posterTitles = listOf<String>("cheto film","drygoe kino","pochti kak kino","vrode eto kino",
            "kak kino no da","tipo nazvanie","tozhe cheto")

        public fun getRandomPosterId(): Int {
            return when ((1..5).random()) {
                1 -> R.drawable.poster1
                2 -> R.drawable.poster2
                3 -> R.drawable.poster3
                4 -> R.drawable.poster4
                5 -> R.drawable.poster5
                else -> R.drawable.poster1
            }
        }

        public fun getRandomRData(): RData {
            return RData(
                getRandomPosterId(),
                posterTitles.random(),
                "poster description\n To connect to your server, copy the server address and enter it in your Minecraft client, as a new server or with \"Direct Connect\". You can find the server address on the server page."
            )
        }

        public fun getRandomList(count: Int): MutableList<RData>? {
            return Stream.generate {
                getRandomRData()
            }.limit(count.toLong()).collect(Collectors.toList())
        }
    }
}