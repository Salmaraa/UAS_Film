package com.example.uas_film

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_film.databinding.ActivityHomeAdminBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeAdmin : AppCompatActivity(), MovieItemClickListener {
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var itemAdapterMovie: AdminMovieAdapter
    private lateinit var itemListMovie: ArrayList<AdminData>
    private lateinit var recyclerViewItem: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private val firestore = FirebaseFirestore.getInstance()
    private val moviesCollection = firestore.collection("Movie")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi RecyclerView dan Adapter untuk daftar film
        recyclerViewItem = binding.listMovie
        recyclerViewItem.setHasFixedSize(true)
        recyclerViewItem.layoutManager = LinearLayoutManager(this)

        itemListMovie = arrayListOf()
        itemAdapterMovie = AdminMovieAdapter(itemListMovie,this)
        recyclerViewItem.adapter = itemAdapterMovie

        // Inisialisasi SharedPreferences untuk menyimpan status login
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.fabAddMovie.setOnClickListener {
            intent = Intent(this, UploadAdminActivity::class.java)
            startActivity(intent)
        }

        val logoutAdminButton: ImageButton = findViewById(R.id.admin_logout)
        logoutAdminButton.setOnClickListener {
            logoutUser()
        }

        // Mengambil data film dari Firestore dan menampilkan dalam RecyclerView
        moviesCollection.get().addOnSuccessListener { querySnapshots ->
            val movies = ArrayList<AdminData>()

            for (doc in querySnapshots) {
                val movie = doc.toObject(AdminData::class.java)
                movies.add(movie)
            }

            itemAdapterMovie.setData(movies)
            itemAdapterMovie.notifyDataSetChanged()
        }
    }

    private fun logoutUser() {
        // Mengubah nilai userType menjadi "guest" di SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("userType", "needlogin")
        editor.apply()

        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onEditButtonClick(movie: AdminData) {
        val intent = Intent(this, AdminEditActivity::class.java)
        intent.putExtra("selectedMovie", movie)
        startActivity(intent)
    }

    override fun onDeleteButtonClick(movie: AdminData) {
        val movieId = movie.id

        moviesCollection.document(movieId)
            .delete()
            .addOnSuccessListener {
                val imageUrl = movie.image
                if (imageUrl.isNotEmpty()) {
                    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                    storageReference.delete().addOnSuccessListener {
                        Log.d("HomeAdmin", "Image deleted successfully")
                    }.addOnFailureListener {
                        Log.e("HomeAdmin", "Error deleting image: $it")
                    }
                }

                // Menghapus item dari daftar dan memperbarui RecyclerView
                itemListMovie.remove(movie)
                itemAdapterMovie.notifyDataSetChanged()

                Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("HomeAdmin", "Error deleting movie: $it")
                Toast.makeText(this, "Failed to delete movie", Toast.LENGTH_SHORT).show()
            }
    }

    // logout action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.admin_logout -> {
                logoutUser()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}