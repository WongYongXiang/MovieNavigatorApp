package com.ltu.moviedb.movienavigator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ltu.moviedb.movienavigator.MovieDBApplication
import com.ltu.moviedb.movienavigator.database.MoviesRepository
import com.ltu.moviedb.movienavigator.database.SavedMoviesRepository
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.model.MovieReviewsResponse
import com.ltu.moviedb.movienavigator.model.NetworkMonitor
import com.ltu.moviedb.movienavigator.model.VideoModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>) : MovieListUiState
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
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set

    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    var movieReviewsUiState by mutableStateOf<MovieReviewsUiState>(MovieReviewsUiState.Loading)
        private set

    var movieVideosUiState by mutableStateOf<MovieVideosUiState>(MovieVideosUiState.Loading)
        private set

    private val _connectionState = MutableStateFlow(true)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    private var lastFetchedListType: String? = null
    private var currentListType: String = "popular"

    init {
        getPopularMovies()
        startNetworkMonitoring()
    }

    private fun startNetworkMonitoring() {
        viewModelScope.launch {
            networkMonitor.isConnected.asFlow().collect { isConnected ->
                _connectionState.value = isConnected
                if (isConnected) {
                    // Refresh the current list type when connection is restored
                    when (currentListType) {
                        "popular" -> getPopularMovies()
                        "top_rated" -> getTopRatedMovies()
                        "saved" -> getSavedMovies()
                    }
                }
            }
        }
    }


    fun getTopRatedMovies() {
        viewModelScope.launch {
            currentListType = "top_rated"
            movieListUiState = MovieListUiState.Loading
            try {
                _connectionState.value = true
                val movies = moviesRepository.getTopRatedMovies()
                movieListUiState = MovieListUiState.Success(movies.results)
                lastFetchedListType = "top_rated"
            } catch (e: IOException) {
                _connectionState.value = false
                movieListUiState = MovieListUiState.Error
            } catch (e: HttpException) {
                _connectionState.value = true
                movieListUiState = MovieListUiState.Error
            }
        }
    }

    fun getPopularMovies() {
        viewModelScope.launch {
            currentListType = "popular"
            movieListUiState = MovieListUiState.Loading
            try {
                _connectionState.value = true
                val movies = moviesRepository.getPopularMovies()
                movieListUiState = MovieListUiState.Success(movies.results)
                lastFetchedListType = "popular"
            } catch (e: IOException) {
                _connectionState.value = false
                movieListUiState = MovieListUiState.Error
            } catch (e: HttpException) {
                _connectionState.value = true
                movieListUiState = MovieListUiState.Error
            }
        }
    }

    fun setSelectedMovie(movie: Movie) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            selectedMovieUiState = try {
                SelectedMovieUiState.Success(
                    movie,
                    savedMoviesRepository.getMovie(movie.id) != null
                )
            } catch (e: IOException) {
                SelectedMovieUiState.Error
            } catch (e: HttpException) {
                SelectedMovieUiState.Error
            }
        }
    }

    fun getSavedMovies() {
        viewModelScope.launch {
            currentListType = "saved" // Update current list type
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getSavedMovies())
            } catch (e: IOException) {
                MovieListUiState.Error
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as MovieDBApplication)
                MovieDBViewModel(
                    moviesRepository = application.container.moviesRepository,
                    savedMoviesRepository = application.container.savedMoviesRepository,
                    networkMonitor = application.container.networkMonitor
                )
            }
        }
    }
}