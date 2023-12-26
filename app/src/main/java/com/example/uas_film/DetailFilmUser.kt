package com.example.uas_film

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uas_film.HomeUserFragment
import com.example.uas_film.R
import com.example.uas_film.databinding.ActivityDetailFilmUserBinding

class DetailFilmUser : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFilmUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFilmUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imgId = intent.getStringExtra("imgId")

        // Display data in the layout
        binding.detailJudulFilm.text = title
        binding.detailDeskripsiFilm.text = description

        // Load image using Glide
        Glide.with(this)
            .load(imgId)
            .placeholder(R.drawable.image_upload) // Placeholder image while loading
            .error(R.drawable.image_upload) // Image to display in case of error
            .into(binding.detailImage)

        // Handle arrow back click
        binding.arrowBack.setOnClickListener {
            // Kembali ke HomeUserFragment menggunakan Intent
            val intent = Intent(this, HomeUserFragment::class.java)
            startActivity(intent)

            // Akhiri aktivitas saat ini
            finish()
        }
    }
}
