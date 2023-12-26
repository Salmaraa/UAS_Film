package com.example.uas_film

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.uas_film.databinding.ActivityUserBottomBinding

class UserBottomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBottomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeUserFragment())



        binding.bottomNavbar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> replaceFragment(HomeUserFragment())
                R.id.nav_profile -> replaceFragment(ProfileUserFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }
}