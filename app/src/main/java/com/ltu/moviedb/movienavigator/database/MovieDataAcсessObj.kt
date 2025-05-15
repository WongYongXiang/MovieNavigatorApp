package com.ltu.moviedb.movienavigator.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ltu.moviedb.movienavigator.model.Movie

@Dao
interface MovieDataAccessObj {
    @Query("SELECT * FROM favorite_movies" )
    suspend fun getSavedMovies(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movie)

    @Query("SELECT * FROM favorite_movies WHERE id = :id" )
    suspend fun getMovie(id: Long): Movie

    @Query("DELETE FROM favorite_movies WHERE id = :id")
    suspend fun deleteMovie(id: Long)
}
