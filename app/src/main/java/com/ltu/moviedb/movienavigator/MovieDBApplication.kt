package com.ltu.moviedb.movienavigator

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ltu.moviedb.movienavigator.database.AppContainer
import com.ltu.moviedb.movienavigator.database.DefaultAppContainer
import com.ltu.moviedb.movienavigator.model.MovieListRefreshWorker
import java.util.concurrent.TimeUnit

class MovieDBApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        scheduleRefresh()
    }

    private fun scheduleRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<MovieListRefreshWorker>(
            4, TimeUnit.HOURS // Refresh every 4 hours
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            MovieListRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if it exists
            request
        )
    }
}