package com.ltu.moviedb.movienavigator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ltu.moviedb.movienavigator.utils.Constants
import com.ltu.moviedb.movienavigator.utils.Genre
import com.ltu.moviedb.movienavigator.viewmodel.MovieDBViewModel
import com.ltu.moviedb.movienavigator.viewmodel.SelectedMovieUiState


@Composable
fun MovieDetailScreen(
    selectedMovieUiState: SelectedMovieUiState,
    movieDBViewModel: MovieDBViewModel,
    modifier: Modifier = Modifier,
    onNavigateToThirdScreen: () -> Unit = {}
) {
    when (selectedMovieUiState) {
        is SelectedMovieUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Backdrop Image
                item {
                    AsyncImage(
                        model = Constants.BACKDROP_IMAGE_BASE_URL + Constants.BACKDROP_IMAGE_BASE_WIDTH + selectedMovieUiState.movie.backdropPath,
                        contentDescription = selectedMovieUiState.movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f/9f),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }

                // Movie Title
                item {
                    Text(
                        text = selectedMovieUiState.movie.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }

                // Release Date
                item {
                    Text(
                        text = selectedMovieUiState.movie.releaseDate,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }

                // Genres
                item {
                    Text(
                        text = "Genres: ${Genre.getGenreNames(selectedMovieUiState.movie.genres).joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }

                // Overview
                item {
                    Text(
                        text = selectedMovieUiState.movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }

                item {
                    Text(
                        text = "Favourite",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(checked = selectedMovieUiState.isFavorite, onCheckedChange ={
                        if (it)
                            movieDBViewModel.saveMovie(selectedMovieUiState.movie)
                        else
                            movieDBViewModel.deleteMovie(selectedMovieUiState.movie)
                    })
                }
                // Button
                item {
                    Button(
                        onClick = onNavigateToThirdScreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Go to Trailer and Reviews")
                    }
                }
            }
        }
        is SelectedMovieUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is SelectedMovieUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading movie details",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}