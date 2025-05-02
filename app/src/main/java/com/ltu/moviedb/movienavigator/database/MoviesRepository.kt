package com.ltu.moviedb.movienavigator.database

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