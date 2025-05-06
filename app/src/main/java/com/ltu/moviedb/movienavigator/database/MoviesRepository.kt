package com.ltu.moviedb.movienavigator.database

import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.model.MovieResponse
import com.ltu.moviedb.movienavigator.model.MovieReviewsResponse
import com.ltu.moviedb.movienavigator.model.VideoModels
import com.ltu.moviedb.movienavigator.network.MovieDBApiService

interface MoviesRepository {
    suspend fun getPopularMovies(): MovieResponse
    suspend fun getTopRatedMovies(): MovieResponse
    suspend fun getMovieReviews(movieId: Long): MovieReviewsResponse
    suspend fun getMovieVideos(movieId: Long): VideoModels
}

class NetworkMoviesRepository(private val apiService: MovieDBApiService) : MoviesRepository {
    override suspend fun getPopularMovies(): MovieResponse {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRatedMovies(): MovieResponse {
        return apiService.getTopRatedMovies()
    }

    override suspend fun getMovieReviews(movieId: Long): MovieReviewsResponse {
        return apiService.getMovieReviews(movieId)
    }

    override suspend fun getMovieVideos(movieId: Long): VideoModels {
        return apiService.getMovieVideos(movieId)
    }
}

interface SavedMoviesRepository{
    suspend fun getSavedMovies(): List<Movie>
    suspend fun insertMovie(movie: Movie)
    suspend fun getMovie(id: Long): Movie
    suspend fun deleteMovie(movie: Movie)
}

class FavoriteMoviesRepository(private val movieDAO: MovieDataAcсessObj): SavedMoviesRepository{
    override suspend fun getSavedMovies(): List<Movie> {
        return movieDAO.getSavedMovies()
    }

    override suspend fun insertMovie(movie: Movie) {
        movieDAO.insertMovie(movie)
    }

    override suspend fun getMovie(id: Long): Movie {
        return movieDAO.getMovie(id)
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDAO.deleteMovie(movie.id)
    }
}