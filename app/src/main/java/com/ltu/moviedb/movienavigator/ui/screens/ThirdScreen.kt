package com.ltu.moviedb.movienavigator.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ltu.moviedb.movienavigator.model.Review
import com.ltu.moviedb.movienavigator.viewmodel.MovieDBViewModel
import com.ltu.moviedb.movienavigator.viewmodel.MovieReviewsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(
    movieId: Long? = null,
    viewModel: MovieDBViewModel = viewModel(factory = MovieDBViewModel.Factory),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val reviewsState = viewModel.movieReviewsUiState

    // Video player setup - this runs independently
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(
                Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            )
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Launch the review fetch in a coroutine that won't block the UI
    LaunchedEffect(movieId) {
        movieId?.let {
            viewModel.fetchReviews(it)
        }
    }

    Scaffold(

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Video Player Section - this renders immediately
            Text(
                text = "Trailer",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reviews Section - this loads independently
            Text(
                text = "Reviews",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // This will show the appropriate state while video plays
            ReviewsContent(reviewsState = reviewsState)
        }
    }
}

@Composable
private fun ReviewsContent(reviewsState: MovieReviewsUiState) {
    when (reviewsState) {
        is MovieReviewsUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is MovieReviewsUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reviewsState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is MovieReviewsUiState.Success -> {
            val reviews = reviewsState.reviews.results
            if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reviews available yet")
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)  // Correct usage without parentheses
                    ) {
                        items(reviews) { review ->
                            ReviewCard(review = review)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author and Rating as text
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = review.author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                review.authorDetails.rating?.let { rating ->
                    Text(
                        text = "Rating: ${"%.1f".format(rating)}/10",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Review Content
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Review Date
            Text(
                text = "Posted on ${review.createdAt.substring(0, 10)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

