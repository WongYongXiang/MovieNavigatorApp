package com.ltu.moviedb.movienavigator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.utils.Constants
import com.ltu.moviedb.movienavigator.viewmodel.MovieListUiState

@Composable
fun MovieGridScreen(
    movieListUiState: MovieListUiState,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
    ) {
        when(movieListUiState) {
            is MovieListUiState.Success -> {
                items(movieListUiState.movies) { movie ->
                    MovieGridItem(
                        movie = movie,
                        onMovieListItemClicked,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            is MovieListUiState.Loading -> {
                item {
                    Text(
                        text = "Loading...",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is MovieListUiState.Error -> {
                item {
                    Text(
                        text = "Error: Something went wrong!",
                        modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun MovieGridItem(
    movie: Movie,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = { onMovieListItemClicked(movie) }
    ) {
        Column {
            AsyncImage(
                model = "${Constants.POSTER_IMAGE_BASE_URL}${Constants.POSTER_IMAGE_BASE_WIDTH}${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2/3f),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = movie.releaseDate.take(4), // Show just year
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}