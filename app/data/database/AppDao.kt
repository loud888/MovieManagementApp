package com.example.moviemanagement.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moviemanagement.data.MovieWithInfo
import com.example.moviemanagement.data.entity.Cinema
import com.example.moviemanagement.data.entity.Genre
import com.example.moviemanagement.data.entity.Movie

@Dao
interface AppDao {
    // Genre
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenre(genre: Genre)

    @Query("SELECT * FROM genre")
    fun getAllGenres(): LiveData<List<Genre>>

    @Query("SELECT * FROM genre WHERE name LIKE '%' || :query || '%' OR categories LIKE '%' || :query || '%'")
    fun searchGenres(query: String): LiveData<List<Genre>>

    // Cinema
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCinema(cinema: Cinema)

    @Query("SELECT * FROM cinema")
    fun getAllCinemas(): LiveData<List<Cinema>>

    // Movie
    @Insert
    suspend fun insertMovie(movie: Movie): Long

    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Transaction
    @Query("SELECT * FROM movie ORDER BY releaseDate DESC, ticketPrice DESC")
    fun getAllMoviesWithInfo(): LiveData<List<MovieWithInfo>>

    @Transaction
    @Query("SELECT * FROM movie WHERE releaseDate BETWEEN :fromDate AND :toDate ORDER BY releaseDate DESC, ticketPrice DESC")
    fun getMoviesByDateRangeWithInfo(fromDate: Long, toDate: Long): LiveData<List<MovieWithInfo>>
}
