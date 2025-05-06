package com.ltu.moviedb.movienavigator.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//This converter is to allow genres to be saved under favourites
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromGenreList(value: List<Int>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toGenreList(value: String): List<Int> {
        return json.decodeFromString(value)
    }
}