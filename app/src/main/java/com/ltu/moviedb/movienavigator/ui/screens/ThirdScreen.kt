package com.ltu.moviedb.movienavigator.ui.screens

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Video player setup
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

    LaunchedEffect(movieId) {
        movieId?.let { viewModel.fetchReviews(it) }
    }

    Scaffold(
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .then(
                    if (isLandscape) {
                        Modifier.horizontalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                )
        ) {
            // Video Player Section
            item {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Trailer",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .padding(horizontal = 16.dp)
                    ) {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    player = exoPlayer
                                    useController = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Reviews Section
            item {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when (reviewsState) {
                is MovieReviewsUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is MovieReviewsUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (reviewsState as MovieReviewsUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                is MovieReviewsUiState.Success -> {
                    val reviews = (reviewsState as MovieReviewsUiState.Success).reviews.results
                    if (reviews.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No reviews available yet")
                            }
                        }
                    } else {
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
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Posted on ${review.createdAt.substring(0, 10)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
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
                            .weight(1f)
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




