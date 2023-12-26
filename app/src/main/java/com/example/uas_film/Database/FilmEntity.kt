package com.example.uas_film.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film")
data class FilmEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "judul")
    val title: String,

    @ColumnInfo(name = "deskripsi")
    val description: String,

    @ColumnInfo(name = "alamatGambar")
    val imagePath: String
)