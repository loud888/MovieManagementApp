package com.example.moviemanagement.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.moviemanagement.data.entity.Cinema
import com.example.moviemanagement.data.entity.Genre
import com.example.moviemanagement.data.entity.Movie

data class MovieWithInfo(
    @Embedded val movie: Movie,
    @Relation(parentColumn = "genreId", entityColumn = "id") val genre: Genre,
    @Relation(parentColumn = "cinemaId", entityColumn = "id") val cinema: Cinema
)
