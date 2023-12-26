package com.example.uas_film

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(
    private val movieUserList: ArrayList<AdminData>,
    private val clickListener: MovieItemClickListener
) : RecyclerView.Adapter<UserAdapter.MovieUserViewHolder>() {
    class MovieUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Inisialisasi TextView dan ImageView dari layout item
        val title: TextView? = itemView.findViewById(R.id.mv_judul)
        val image: ImageView? = itemView.findViewById(R.id.mv_poster)
    }

    // Metode ini dipanggil setiap kali ViewHolder perlu ditampilkan dengan data
    override fun onBindViewHolder(holder: MovieUserViewHolder, position: Int) {
        // mengambil posisi tertentu pd list
        val currentItem = movieUserList[position]

        currentItem.image?.let { Log.d("ImageURL", it) }

        holder.title?.text = currentItem.title

        holder.image?.let {
            Glide.with(holder.itemView.context)
                .load(currentItem.image)
                .into(it)
        }
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(currentItem)
        }
    }

    // ketika RecyclerView memerlukan ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieUserViewHolder {
        // Inflate layout item dan kembalikan ViewHolder-nya
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_film_user, parent, false)
        return MovieUserViewHolder(itemView)
    }

    // mengembalikan jumlah total item dalam dataset
    override fun getItemCount(): Int {
        return movieUserList.size
    }

    // mengatur data film dalam adapter
    fun updateMovies(movies: ArrayList<AdminData>) {
        movieUserList.clear()
        movieUserList.addAll(movies)
        // beritahu adapter kalo dataset berubah sehingga dapat melakukan refresh tampilan
        notifyDataSetChanged()
    }
}
