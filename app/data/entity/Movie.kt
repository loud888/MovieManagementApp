package com.example.moviemanagement.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie",
    foreignKeys = [
        ForeignKey(entity = Genre::class, parentColumns = ["id"], childColumns = ["genreId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Cinema::class, parentColumns = ["id"], childColumns = ["cinemaId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "genreId") val genreId: Int,
    @ColumnInfo(name = "cinemaId") val cinemaId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "releaseDate") val releaseDate: Long, // Timestamp
    @ColumnInfo(name = "ticketPrice") val ticketPrice: Double
)
