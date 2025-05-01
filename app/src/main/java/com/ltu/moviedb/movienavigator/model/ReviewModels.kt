package com.ltu.moviedb.movienavigator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieReviewsResponse(
    @SerialName("id")
    val movieId: Int,

    @SerialName("page")
    val page: Int,

    @SerialName("results")
    val results: List<Review>,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("total_results")
    val totalResults: Int
)

@Serializable
data class Review(
    @SerialName("author")
    val author: String,

    @SerialName("author_details")
    val authorDetails: AuthorDetails,

    @SerialName("content")
    val content: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("id") val id: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("url")
    val url: String
)

@Serializable
data class AuthorDetails(
    @SerialName("name")
    val name: String? = null,

    @SerialName("username")
    val username: String,

    @SerialName("avatar_path")
    val avatarPath: String? = null,

    @SerialName("rating")
    val rating: Float?
)