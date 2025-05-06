package com.ltu.moviedb.movienavigator.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "favorite_movies")
data class Movie(
    @PrimaryKey
    @SerialName(value = "id")
    var id: Long = 0L,

    @SerialName(value = "title")
    var title: String,

    @SerialName(value = "poster_path")
    var posterPath: String? = null,

    @SerialName(value = "backdrop_path")
    var backdropPath: String? = null,

    @SerialName(value = "release_date")
    var releaseDate: String,

    @SerialName(value = "overview")
    var overview: String,

    @SerialName(value = "genre_ids")
    var genres: List<Int> = emptyList(),

    var homepage: String? = null,
    var imdbID: String? = null
)
