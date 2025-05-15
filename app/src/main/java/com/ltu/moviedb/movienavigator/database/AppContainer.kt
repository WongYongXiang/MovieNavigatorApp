package com.ltu.moviedb.movienavigator.database

import android.content.Context
import android.net.ConnectivityManager
import com.ltu.moviedb.movienavigator.network.MovieDBApiService
import com.ltu.moviedb.movienavigator.utils.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ltu.moviedb.movienavigator.MovieDBApplication
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.model.MovieResponse
import com.ltu.moviedb.movienavigator.model.MovieReviewsResponse
import com.ltu.moviedb.movienavigator.model.NetworkMonitor
import com.ltu.moviedb.movienavigator.model.VideoModels
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException

interface AppContainer {
    val moviesRepository: MoviesRepository
    val savedMoviesRepository: SavedMoviesRepository
    val networkMonitor: NetworkMonitor
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: MovieDatabase by lazy {
        MovieDatabase.getDatabase(context)
    }

    private val cachedMoviesRepository: CachedMoviesRepository by lazy {
        CachedMoviesRepositoryImpl(database.cachedMovieListDao())
    }

    fun getLoggerInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private val movieDBJson = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(
            okhttp3.OkHttpClient.Builder()
                .addInterceptor(getLoggerInterceptor())
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(movieDBJson.asConverterFactory("application/json".toMediaType()))
        .baseUrl(Constants.MOVIE_LIST_BASE_URL)
        .build()

    private val retrofitService: MovieDBApiService by lazy {
        retrofit.create(MovieDBApiService::class.java)
    }

    override val moviesRepository: MoviesRepository by lazy {
        CachingMoviesRepository(
            networkRepository = NetworkMoviesRepository(retrofitService),
            cachedRepository = cachedMoviesRepository,
            context = context
        )
    }

    override val savedMoviesRepository: SavedMoviesRepository by lazy {
        FavoriteMoviesRepository(database.movieDao())
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(context)
    }
}

// Caching Repository Implementation
class CachingMoviesRepository(
    private val networkRepository: MoviesRepository,
    private val cachedRepository: CachedMoviesRepository,
    private val context: Context
) : MoviesRepository {

    private var lastFetchedListType: String? = null

    override suspend fun getPopularMovies(): MovieResponse {
        return try {
            val movies = networkRepository.getPopularMovies()
            cachedRepository.cachePopularMovies(movies.results)
            cachedRepository.clearCache("top_rated")
            lastFetchedListType = "popular"
            movies
        } catch (e: Exception) {
            if (isOnline(context)) {
                throw e // If online but still error, propagate it
            } else {
                // If offline, return cached data if it's the last fetched list type
                if (lastFetchedListType == "popular") {
                    MovieResponse(
                        page = 1,
                        results = cachedRepository.getPopularMovies(),
                        totalPages = 1,
                        totalResults = cachedRepository.getPopularMovies().size.toInt()
                    )
                } else {
                    throw IOException("No internet connection and no cached data available")
                }
            }
        }
    }

    override suspend fun getTopRatedMovies(): MovieResponse {
        return try {
            val movies = networkRepository.getTopRatedMovies()
            cachedRepository.cacheTopRatedMovies(movies.results)
            cachedRepository.clearCache("popular")
            lastFetchedListType = "top_rated"
            movies
        } catch (e: Exception) {
            if (isOnline(context)) {
                throw e
            } else {
                if (lastFetchedListType == "top_rated") {
                    MovieResponse(
                        page = 1,
                        results = cachedRepository.getTopRatedMovies(),
                        totalPages = 1,
                        totalResults = cachedRepository.getTopRatedMovies().size.toInt()
                    )
                } else {
                    throw IOException("No internet connection and no cached data available")
                }
            }
        }
    }

    override suspend fun getMovieReviews(movieId: Long): MovieReviewsResponse {
        return networkRepository.getMovieReviews(movieId)
    }

    override suspend fun getMovieVideos(movieId: Long): VideoModels {
        return networkRepository.getMovieVideos(movieId)
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

// Cached Movies Repository Interface and Implementation
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
}