package com.example.uas_film

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_film.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handler untuk menangani penundaan dan beralih ke halaman berikutnya setelah splashTimeOut
        Handler().postDelayed({
            // Membuat Intent untuk beralih ke halaman LoginRegister
            val intent = Intent(this, LoginRegisterActivity::class.java)
            startActivity(intent)

            // Menutup aktivitas saat ini agar tidak bisa kembali ke splash screen
            finish()
        }, splashTimeOut)
    }
}
