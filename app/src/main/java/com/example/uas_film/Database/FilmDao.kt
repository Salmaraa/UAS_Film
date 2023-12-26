package com.example.uas_film.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FilmDao {
    @Insert
    suspend fun insertMovie(movie: FilmEntity)

    @Insert
    fun insertAllMovies(movies: List<FilmEntity>)

    @Query("DELETE FROM film")
    suspend fun deleteAllMovies()

    @Query("SELECT * FROM film")
    suspend fun getAllMovies(): List<FilmEntity>

    @Query("SELECT * FROM film WHERE id = :movieId")
    fun getMovieById(movieId: String): FilmEntity?
}