package com.ltu.moviedb.movienavigator.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.ltu.moviedb.movienavigator.model.Review
import com.ltu.moviedb.movienavigator.viewmodel.MovieDBViewModel
import com.ltu.moviedb.movienavigator.viewmodel.MovieReviewsUiState
import com.ltu.moviedb.movienavigator.viewmodel.MovieVideosUiState
import androidx.core.net.toUri
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(
    movieId: Long? = null,
    viewModel: MovieDBViewModel = viewModel(factory = MovieDBViewModel.Factory),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val videosState = viewModel.movieVideosUiState
    val reviewsState = viewModel.movieReviewsUiState
    val isNetworkAvailable = viewModel.isNetworkAvailable

    var isInitialLoad by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showRefreshToast by remember { mutableStateOf(false) }

    // Track currently selected video
    var selectedVideo by remember { mutableStateOf<String?>(null) }

    // Initial data fetch - runs only once when movieId changes
    LaunchedEffect(movieId) {
        if (movieId != null && isInitialLoad) {
            viewModel.fetchVideos(movieId)
            viewModel.fetchReviews(movieId)
            isInitialLoad = false
        }
    }

    // Handle network changes - only refresh if not initial load
    LaunchedEffect(isNetworkAvailable) {
        if (isNetworkAvailable && !isInitialLoad &&
            (videosState is MovieVideosUiState.Error || reviewsState is MovieReviewsUiState.Error)) {
            isRefreshing = true
            movieId?.let {
                viewModel.fetchVideos(it)
                viewModel.fetchReviews(it)
            }
        }
    }

    // Handle refresh completion
    LaunchedEffect(videosState, reviewsState) {
        if (isRefreshing && videosState !is MovieVideosUiState.Loading &&
            reviewsState !is MovieReviewsUiState.Loading) {
            isRefreshing = false
            showRefreshToast = true
        }
    }

    // Show toast when refresh completes
    if (showRefreshToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(
                context,
                "Data refreshed with new network connection",
                Toast.LENGTH_SHORT
            ).show()
            showRefreshToast = false
        }
    }

    Scaffold() { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Videos Section
                item {
                    Column {
                        // Show refreshing indicator if needed
                        if (isRefreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Text(
                            text = "Videos",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )

                        when (videosState) {
                            is MovieVideosUiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is MovieVideosUiState.Error -> {
                                Text(
                                    text = videosState.message,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            is MovieVideosUiState.Success -> {
                                val videos = videosState.videos.results
                                if (videos.isEmpty()) {
                                    Text(
                                        text = "No videos available",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    // Auto-select first video if none selected
                                    if (selectedVideo == null) {
                                        selectedVideo = videos.first().key
                                    }

                                    // YouTube Player
                                    selectedVideo?.let { videoId ->
                                        YouTubePlayerEmbed(
                                            videoId = videoId,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(16f / 9f)
                                                .padding(16.dp)
                                        )
                                    }

                                    // Video List
                                    Text(
                                        text = "Available Videos (${videos.size})",
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    LazyRow(
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        items(videos) { video ->
                                            VideoThumbnailCard(
                                                video = video,
                                                isSelected = video.key == selectedVideo,
                                                onClick = {
                                                    selectedVideo = video.key
                                                },
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
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
                                    text = reviewsState.message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    is MovieReviewsUiState.Success -> {
                        val reviews = reviewsState.reviews.results
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

            // Show network status banner at the bottom
            if (!isNetworkAvailable) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "No internet connection - please check connection",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

// YouTube Player Component
@Composable
fun YouTubePlayerEmbed(
    videoId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = modifier
    )
}

// Video Thumbnail Card
@Composable
fun VideoThumbnailCard(
    video: com.ltu.moviedb.movienavigator.model.Video,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://img.youtube.com/vi/${video.key}/0.jpg",
                    contentDescription = video.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Video info
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${video.type} • ${video.site}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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
