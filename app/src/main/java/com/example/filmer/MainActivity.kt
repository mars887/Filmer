package com.example.filmer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.filmer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.butt1.setOnClickListener {
            Toast.makeText(this,R.string.button_1_text,Toast.LENGTH_SHORT).show()
        }
        binding.butt2.setOnClickListener {
            Toast.makeText(this,R.string.button_2_text,Toast.LENGTH_SHORT).show()
        }
        binding.butt3.setOnClickListener {
            Toast.makeText(this,R.string.button_3_text,Toast.LENGTH_SHORT).show()
        }
        binding.butt4.setOnClickListener {
            Toast.makeText(this,R.string.button_4_text,Toast.LENGTH_SHORT).show()
        }
        binding.butt5.setOnClickListener {
            Toast.makeText(this,R.string.button_5_text,Toast.LENGTH_SHORT).show()
        }

    }
}