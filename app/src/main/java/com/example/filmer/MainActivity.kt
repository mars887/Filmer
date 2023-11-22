package com.example.filmer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuView
import com.example.filmer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                        } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                true
            }
            setNavigationOnClickListener {
                Toast.makeText(this@MainActivity, "menu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}