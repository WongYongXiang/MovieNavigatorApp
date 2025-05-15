package com.ltu.moviedb.movienavigator.database

import com.ltu.moviedb.movienavigator.model.Movie
/***
interface CachedMoviesRepository {
    suspend fun getPopularMovies(): List<Movie>
    suspend fun getTopRatedMovies(): List<Movie>
    suspend fun cachePopularMovies(movies: List<Movie>)
    suspend fun cacheTopRatedMovies(movies: List<Movie>)
    suspend fun clearCache(listType: String)
}

class CachedMoviesRepositoryImpl(
    private val cachedMovieListDao: CachedMovieListDao
) : CachedMoviesRepository {
    override suspend fun getPopularMovies(): List<Movie> {
        return cachedMovieListDao.get("popular")?.movies ?: emptyList()
    }

    override suspend fun getTopRatedMovies(): List<Movie> {
        return cachedMovieListDao.get("top_rated")?.movies ?: emptyList()
    }

    override suspend fun cachePopularMovies(movies: List<Movie>) {
        cachedMovieListDao.insert(CachedMovieList("popular", movies))
    }

    override suspend fun cacheTopRatedMovies(movies: List<Movie>) {
        cachedMovieListDao.insert(CachedMovieList("top_rated", movies))
    }

    override suspend fun clearCache(listType: String) {
        cachedMovieListDao.delete(listType)
    }
}***/