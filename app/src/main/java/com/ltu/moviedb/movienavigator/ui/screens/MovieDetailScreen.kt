package com.ltu.moviedb.movienavigator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.utils.Constants
import com.ltu.moviedb.movienavigator.utils.HomepageLink
import com.ltu.moviedb.movienavigator.utils.IMDBLink

@Composable
fun MovieDetailScreen(
    movie: Movie,
    modifier: Modifier = Modifier,
    onNavigateToThirdScreen: () -> Unit ={}
) {
    Column {
        Box {
            AsyncImage(
                model = Constants.BACKDROP_IMAGE_URL + Constants.BACKDROP_IMAGE_WIDTH+ movie.backdropPath,
                contentDescription = movie.title,
                modifier = Modifier, //complete weight
                contentScale = ContentScale.Crop
            )
        }

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
            text = "Genres: ${movie.genres.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.size(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomepageLink(homepage = movie.homepage)
            IMDBLink(imdbId = movie.imdbID)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis

        )
        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = onNavigateToThirdScreen,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ){
            Text("Go to Third Screen")
        }

    }
}