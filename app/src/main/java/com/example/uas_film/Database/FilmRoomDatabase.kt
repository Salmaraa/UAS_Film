package com.example.uas_film.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FilmEntity::class], version = 1, exportSchema = false)
abstract class FilmRoomDatabase : RoomDatabase() {
    // akses ke DAO untuk melakukan operasi database
    abstract fun movieDao(): FilmDao

    companion object {
        @Volatile
        private var INSTANCE: FilmRoomDatabase? = null

        // Fungsi getDatabase untuk mendapatkan instance database Room.
        fun getDatabase(context: Context): FilmRoomDatabase? {
            if (INSTANCE == null){
                // memastikan bahwa hanya satu thread yang dapat membuat instance database
                synchronized(FilmRoomDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        FilmRoomDatabase::class.java, "MovieLocal"
                    )
                        .build()
                }
            }
            return INSTANCE

        }
    }
}