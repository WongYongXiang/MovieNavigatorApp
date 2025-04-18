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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.ui.theme.MovieNavigatorTheme
import com.ltu.moviedb.movienavigator.utils.Constants

@Composable
fun MovieListScreen(movieList: List<Movie>,
                    onMovieListItemClicked:  (Movie) -> Unit,
                    modifier: Modifier = Modifier){
    LazyColumn(modifier = modifier){
        items(movieList) { movie ->
            MovieListItemCard(
                movie = movie,
                onMovieListItemClicked,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MovieListItemCard(
    movie: Movie,
    onMovieListItemClicked:  (Movie) -> Unit,
    modifier: Modifier = Modifier){
    Card(
        modifier = modifier,
        onClick =  {
            onMovieListItemClicked(movie)
        }
    ){
        Row {
            Box {
                AsyncImage(
                    model = Constants.POSTER_IMAGE_URL + Constants.POSTER_IMAGE_WIDTH + movie.posterPath,
                    contentDescription = movie.title,
                    modifier = modifier
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
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieItemPreview() {
    MovieNavigatorTheme {
        MovieListItemCard(
            movie = Movie(
                id = 1,
                title = "In the Lost Lands",
                posterPath = "/dDlfjR7gllmr8HTeN6rfrYhTdwX.jpg",
                backdropPath = "/2Nti3gYAX513wvhp8IiLL6ZDyOm.jpg",
                releaseDate = "2025-02-27",
                overview = "A queen sends the powerful and feared sorceress Gray Alys to the ghostly wilderness of the Lost Lands in search of a magical power, where the sorceress and her guide, the drifter Boyce must outwit and outfight man and demon."
            ), {}
        )
    }
}