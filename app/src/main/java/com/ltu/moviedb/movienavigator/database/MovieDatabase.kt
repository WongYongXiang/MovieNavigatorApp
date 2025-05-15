package com.ltu.moviedb.movienavigator.database


import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ltu.moviedb.movienavigator.model.Movie
import kotlin.also
import kotlin.jvm.java


@Database(
    entities = [Movie::class, CachedMovieList::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDataAccessObj
    abstract fun cachedMovieListDao(): CachedMovieListDao

    companion object {
        @Volatile
        private var Instance: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MovieDatabase::class.java, "movie_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
@Entity(tableName = "cached_movie_lists")
data class CachedMovieList(
    @PrimaryKey
    val listType: String, // "popular" or "top_rated"
    val movies: List<Movie>,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface CachedMovieListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cachedList: CachedMovieList)

    @Query("SELECT * FROM cached_movie_lists WHERE listType = :listType")
    suspend fun get(listType: String): CachedMovieList?

    @Query("DELETE FROM cached_movie_lists WHERE listType = :listType")
    suspend fun delete(listType: String)
}
