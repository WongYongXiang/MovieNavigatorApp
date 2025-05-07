package com.ltu.moviedb.movienavigator.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ltu.moviedb.movienavigator.MovieDBApplication
import com.ltu.moviedb.movienavigator.database.MoviesRepository
import com.ltu.moviedb.movienavigator.database.SavedMoviesRepository
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.model.MovieReviewsResponse
import com.ltu.moviedb.movienavigator.model.VideoModels

import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>, val isFromCache: Boolean = false) : MovieListUiState
    object Error : MovieListUiState
    object Loading : MovieListUiState
}

sealed interface SelectedMovieUiState {
    data class Success(val movie: Movie, val isFavorite: Boolean) : SelectedMovieUiState
    object Error : SelectedMovieUiState
    object Loading : SelectedMovieUiState
}

sealed interface MovieReviewsUiState {
    object Loading : MovieReviewsUiState
    data class Success(val reviews: MovieReviewsResponse) : MovieReviewsUiState
    data class Error(val message: String) : MovieReviewsUiState
}

sealed interface MovieVideosUiState {
    object Loading : MovieVideosUiState
    data class Success(val videos: VideoModels) : MovieVideosUiState
    data class Error(val message: String) : MovieVideosUiState
}


class MovieDBViewModel(
    private val moviesRepository: MoviesRepository,
    private val savedMoviesRepository: SavedMoviesRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set

    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    var movieReviewsUiState by mutableStateOf<MovieReviewsUiState>(MovieReviewsUiState.Loading)
        private set

    var movieVideosUiState by mutableStateOf<MovieVideosUiState>(MovieVideosUiState.Loading)
        private set

    private var currentListType: CurrentListType = CurrentListType.POPULAR
    private var cachedMovies: List<Movie>? = null
    private var cachedListType: CurrentListType? = null
    private var currentMovieId: Long? = null

    var isNetworkAvailable by mutableStateOf(false)
        private set

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isNetworkAvailable = true
            refreshCurrentList() //Auto-refresh movie list when network returns
            currentMovieId?.let { id ->
                fetchVideos(id) //Auto-refresh videos when network returns
                fetchReviews(id) //Auto-refresh reviews when network returns
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isNetworkAvailable = false
        }
    }

    init {
        registerNetworkCallback()
        getPopularMovies()
    }

    private fun registerNetworkCallback() {
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        } catch (e: Exception) {
            isNetworkAvailable = false
        }
    }

    private fun refreshCurrentList() {
        when (currentListType) {
            CurrentListType.POPULAR -> getPopularMovies()
            CurrentListType.TOP_RATED -> getTopRatedMovies()
            CurrentListType.SAVED -> getSavedMovies()
        }
    }

    fun getTopRatedMovies() {
        currentListType = CurrentListType.TOP_RATED
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            try {
                val movies = if (isNetworkAvailable) {
                    moviesRepository.getTopRatedMovies().results.also {
                        // Cache only the current list
                        cachedMovies = it
                        cachedListType = CurrentListType.TOP_RATED
                    }
                } else {
                    // cache if it matches current list type
                    if (cachedListType == CurrentListType.TOP_RATED) {
                        cachedMovies ?: throw IOException("No cached data available")
                    } else {
                        throw IOException("No cached data for top-rated movies")
                    }
                }
                movieListUiState = MovieListUiState.Success(
                    movies = movies,
                    isFromCache = !isNetworkAvailable
                )
            } catch (e: IOException) {
                movieListUiState = if (cachedListType == CurrentListType.TOP_RATED && cachedMovies?.isNotEmpty() == true) {
                    MovieListUiState.Success(
                        movies = cachedMovies!!,
                        isFromCache = true
                    )
                } else {
                    MovieListUiState.Error
                }
            } catch (e: HttpException) {
                movieListUiState = MovieListUiState.Error
            }
        }
    }

    fun getPopularMovies() {
        currentListType = CurrentListType.POPULAR
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            try {
                val movies = if (isNetworkAvailable) {
                    moviesRepository.getPopularMovies().results.also {
                        // Cache only the current list
                        cachedMovies = it
                        cachedListType = CurrentListType.POPULAR
                    }
                } else {
                    // Only use cache if it matches current list type
                    if (cachedListType == CurrentListType.POPULAR) {
                        cachedMovies ?: throw IOException("No cached data available")
                    } else {
                        throw IOException("No cached data for popular movies")
                    }
                }
                movieListUiState = MovieListUiState.Success(
                    movies = movies,
                    isFromCache = !isNetworkAvailable
                )
            } catch (e: IOException) {
                movieListUiState = if (cachedListType == CurrentListType.POPULAR && cachedMovies?.isNotEmpty() == true) {
                    MovieListUiState.Success(
                        movies = cachedMovies!!,
                        isFromCache = true
                    )
                } else {
                    MovieListUiState.Error
                }
            } catch (e: HttpException) {
                movieListUiState = MovieListUiState.Error
            }
        }
    }

    fun getSavedMovies() {
        currentListType = CurrentListType.SAVED
        // Clear cache when switching to saved movies
        cachedMovies = null
        cachedListType = null
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getSavedMovies())
            } catch (e: IOException) {
                MovieListUiState.Error
            }
        }
    }

    fun setSelectedMovie(movie: Movie) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            selectedMovieUiState = try {
                SelectedMovieUiState.Success(movie,
                    savedMoviesRepository.getMovie(movie.id) != null)
            } catch (e: IOException) {
                SelectedMovieUiState.Error
            } catch (e: HttpException) {
                SelectedMovieUiState.Error
            }
        }
    }

    fun saveMovie(movie: Movie) {
        viewModelScope.launch {
            savedMoviesRepository.insertMovie(movie)
            selectedMovieUiState = SelectedMovieUiState.Success(movie, isFavorite = true)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            savedMoviesRepository.deleteMovie(movie)
            selectedMovieUiState = SelectedMovieUiState.Success(movie, isFavorite = false)
        }
    }

    fun fetchReviews(movieId: Long) {
        currentMovieId = movieId //Store current movie ID for auto-refresh
        viewModelScope.launch {
            movieReviewsUiState = MovieReviewsUiState.Loading
            try {
                val response = moviesRepository.getMovieReviews(movieId)
                if (response.results.isNotEmpty()) {
                    movieReviewsUiState = MovieReviewsUiState.Success(response)
                } else {
                    movieReviewsUiState = MovieReviewsUiState.Error("No reviews available")
                }
            } catch (e: IOException) {
                movieReviewsUiState = MovieReviewsUiState.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                movieReviewsUiState = MovieReviewsUiState.Error("Server error: ${e.message}")
            } catch (e: Exception) {
                movieReviewsUiState = MovieReviewsUiState.Error("Unknown error: ${e.message}")
            }
        }
    }

    fun fetchVideos(movieId: Long) {
        currentMovieId = movieId //Store current movie ID for auto-refresh
        viewModelScope.launch {
            movieVideosUiState = MovieVideosUiState.Loading
            try {
                val response = moviesRepository.getMovieVideos(movieId)
                movieVideosUiState = MovieVideosUiState.Success(response)
            } catch (e: Exception) {
                movieVideosUiState = MovieVideosUiState.Error(e.message ?: "Failed to load videos")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
                val moviesRepository = application.container.moviesRepository
                val savedMoviesRepository = application.container.savedMoviesRepository
                val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                MovieDBViewModel(
                    moviesRepository = moviesRepository,
                    savedMoviesRepository = savedMoviesRepository,
                    connectivityManager = connectivityManager
                )
            }
        }
    }

    private enum class CurrentListType {
        POPULAR, TOP_RATED, SAVED
    }
}