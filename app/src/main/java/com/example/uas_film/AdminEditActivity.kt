package com.example.uas_film

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uas_film.databinding.ActivityAdminEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable

class AdminEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminEditBinding
    private lateinit var movie: AdminData
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().getReference("images")
    private var imgPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data yang dikirim dari HomeAdmin
        movie = intent.getSerializableExtra("selectedMovie") as AdminData

        // Menampilkan di UI
        binding.edtJudulFilm.setText(movie.title)
        binding.edtDesc.setText(movie.desc)

        if (movie.image.isNotEmpty()) {
            Glide.with(this).load(movie.image).into(binding.edtPosterFilm)
        }

        binding.uploadButton.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, 1)
        }

        binding.btnEditFilm.setOnClickListener {
            val newTitle = binding.edtJudulFilm.text.toString()
            val newDesc = binding.edtDesc.text.toString()

            movie.title = newTitle
            movie.desc = newDesc

            if (imgPath != null) {
                storageReference.putFile(imgPath!!)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener {
                            val newImageFile = it.toString()
                            movie.image = newImageFile
                            updateMovieData(movie)
                        }
                    }
            } else {
                updateMovieData(movie)
            }
        }
    }

    // Menangani hasil pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imgPath = data.data
            Glide.with(this).load(imgPath).into(binding.edtPosterFilm)
        }
    }

    // Update data pada Firestore
    private fun updateMovieData(movie: AdminData) {
        firestore.collection("Movie")
            .document(movie.id)
            .set(movie)
            .addOnSuccessListener {
                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@AdminEditActivity, HomeAdmin::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show()
            }
    }
}
