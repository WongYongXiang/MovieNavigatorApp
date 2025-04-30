package com.ltu.moviedb.movienavigator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.ui.theme.MovieNavigatorTheme
import com.ltu.moviedb.movienavigator.utils.Constants
import com.ltu.moviedb.movienavigator.utils.Genre
import com.ltu.moviedb.movienavigator.viewmodel.MovieListUiState

@Composable
fun MovieListScreen(
    movieListUiState: MovieListUiState,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier) {
    //scrollable list
    LazyColumn (modifier = modifier) {
        when(movieListUiState) {
            is MovieListUiState.Success -> {
                items(movieListUiState.movies) { movie ->
                    MovieListItemCard(
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
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is MovieListUiState.Error -> {
                item {
                    Text(
                        text = "Error: Something went wrong!",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MovieListItemCard(
    movie: Movie,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier) {
    Card(modifier = modifier,
        onClick = {
            onMovieListItemClicked(movie)
        }) {
        Row {
            Box {
                AsyncImage(
                    model = Constants.POSTER_IMAGE_BASE_URL + Constants.POSTER_IMAGE_BASE_WIDTH + movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .width(92.dp)
                        .height(138.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Genres: ${Genre.getGenreNames(movie.genres).joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis

                )
                Spacer(modifier = Modifier.size(8.dp))
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieItemPreview(){
    MovieNavigatorTheme{
        MovieListItemCard(movie = Movie(
            2,
            "Captain America: Brave New World",
            "/pzIddUEMWhWzfvLI3TwxUG2wGoi.jpg",
            "/gsQJOfeW45KLiQeEIsom94QPQwb.jpg",
            "2025-02-12",
            "When a group of radical activists take over an energy company's annual gala, seizing 300 hostages, an ex-soldier turned window cleaner suspended 50 storeys up on the outside of the building must save those trapped inside, including her younger brother."
        ),  {})
    }
}