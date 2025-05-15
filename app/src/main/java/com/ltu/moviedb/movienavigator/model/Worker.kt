package com.ltu.moviedb.movienavigator.model

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ltu.moviedb.movienavigator.MovieDBApplication

class MovieListRefreshWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val container = (applicationContext as MovieDBApplication).container
            val repository = container.moviesRepository

            // Try to refresh both lists, but only one will be cached
            repository.getPopularMovies()
            repository.getTopRatedMovies()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "MovieListRefreshWorker"
    }
}