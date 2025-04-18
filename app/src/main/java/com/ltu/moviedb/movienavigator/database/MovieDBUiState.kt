package com.ltu.moviedb.movienavigator.database

import com.ltu.moviedb.movienavigator.model.Movie

data class MovieDBUiState(
    val selectedMovie: Movie? = null,
)
