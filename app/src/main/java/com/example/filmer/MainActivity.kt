package com.example.filmer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.filmer.databinding.ActivityMainBinding
import java.util.stream.Collectors
import java.util.stream.Stream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var filmList: ArrayList<RData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filmList = getRandomList(10) as ArrayList<RData>

        if (supportFragmentManager.fragments.size < 1) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, HomeFragment())
                .addToBackStack("home_fragment")
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "favorites", Toast.LENGTH_SHORT).show()
                }

                R.id.selections -> {
                    Toast.makeText(this, "selections", Toast.LENGTH_SHORT).show()
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "watch later", Toast.LENGTH_SHORT).show()
                }
            }
            true
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
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Вы хотите выйти?")
                        .setPositiveButton("Да") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("Нет") { _, _ -> }
                        .show()
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
            .addToBackStack(null)
            .commit()
    }

    fun getData(): ArrayList<RData> = filmList

    companion object {
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
                "poster title",
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