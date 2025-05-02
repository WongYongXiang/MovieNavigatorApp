package com.ltu.moviedb.movienavigator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoModels(
    @SerialName("id") val movieId: Int,
    @SerialName("results") val results: List<Video>
)

@Serializable
data class Video(
    @SerialName("id") val id: String,
    @SerialName("key") val key: String,  //video identifier for the hosting platform
    @SerialName("name") val name: String,
    @SerialName("site") val site: String,  // e.g. "YouTube", "Vimeo"
    @SerialName("type") val type: String,  // e.g. "Trailer", "Teaser"
    @SerialName("official") val official: Boolean
)