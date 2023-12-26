package com.example.uas_film


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_film.Database.FilmDao
import com.example.uas_film.Database.FilmEntity
import com.example.uas_film.Database.FilmRoomDatabase
import com.example.uas_film.databinding.FragmentHomeUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeUserFragment : Fragment(), MovieItemClickListener {

    private lateinit var binding: FragmentHomeUserBinding
    private lateinit var itemAdapterMovie: UserAdapter
    private lateinit var itemListMovie: ArrayList<AdminData>
    private val firestore = FirebaseFirestore.getInstance()
    private val moviesCollection = firestore.collection("Movie")
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerViewItem: RecyclerView
    private lateinit var filmDao: FilmDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Room database
        filmDao = FilmRoomDatabase.getDatabase(requireContext())!!.movieDao()

        // Inisialisasi RecyclerView
        recyclerViewItem = binding.listMovie
        recyclerViewItem.setHasFixedSize(true)
        recyclerViewItem.layoutManager = GridLayoutManager(requireContext(), 2)

        // Inisialisasi MovieAdapter untuk RecyclerView
        itemListMovie = arrayListOf()

        itemAdapterMovie = UserAdapter(itemListMovie, this)
        recyclerViewItem.adapter = itemAdapterMovie

        // Inisialisasi SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mengambil film dari Firestore dan memperbarui RecyclerView
        moviesCollection.get().addOnSuccessListener { querySnapshots ->
            val movies = ArrayList<AdminData>()

            for (doc in querySnapshots) {
                val movie = doc.toObject(AdminData::class.java)
                movies.add(movie)
            }

            Log.d("MovieListSize", "Ukuran film Firestore: ${movies.size}")

            // Memperbarui film dalam RecyclerView
            itemAdapterMovie.updateMovies(movies)
            itemAdapterMovie.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("FirestoreError", "Error mengambil film dari Firestore", e)
        }

        with(binding){
            fetchDataFromFirestoreAndSaveToLocal()
        }
    }

    // Ambil data dari Firestore dan simpan ke database lokal Room
    private fun fetchDataFromFirestoreAndSaveToLocal() {
        Log.d("FirebaseToLocal", "Mulai menyalin data dari Firestore ke Lokal")

        val firestoreMovie = firestore.collection("Movie")
        firestoreMovie.get().addOnSuccessListener { documents ->
            val movieModels = mutableListOf<AdminData>()
            for (document in documents) {
                val movie = document.toObject<AdminData>()
                movieModels.add(movie)
                Log.d("FirebaseToLocalSalmaraa", "$movie")
            }
            val movieEntities = convertToMovieEntity(movieModels)
            CoroutineScope(Dispatchers.IO).launch {
                // Hapus semua film yang ada di database lokal
                filmDao.deleteAllMovies()

                // Masukkan film baru ke dalam database lokal
                for(movie in movieEntities){
                    filmDao.insertMovie(movie)
                }

                Log.d("FirebaseToLocalSalmaraa", "${filmDao.getAllMovies()}")

                withContext(Dispatchers.Main) {
                    Log.d("FirebaseToLocal", "Penyalinan data selesai")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error mengambil dokumen: $exception")
        }
    }

    // mengubah AdminData menjadi FilmEntity
    private fun convertToMovieEntity(movieModels: List<AdminData>): List<FilmEntity> {
        val filmEntities = mutableListOf<FilmEntity>()
        for (movieModel in movieModels) {
            val filmEntity = FilmEntity(
                movieModel.id,
                movieModel.title,
                movieModel.desc,
                movieModel.image
            )
            filmEntities.add(filmEntity)
        }
        return filmEntities
    }

    override fun onEditButtonClick(movie: AdminData) {
    }

    override fun onDeleteButtonClick(movie: AdminData) {
    }
    override fun onItemClick(movie: AdminData) {
        val intent = Intent(requireContext(), DetailFilmUser::class.java)
        intent.putExtra("Judul", movie.title)
        intent.putExtra("Deskripsi", movie.desc)
        intent.putExtra("Idgambar", movie.image)
        startActivity(intent)
    }

}
