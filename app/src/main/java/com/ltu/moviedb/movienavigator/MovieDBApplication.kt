package com.ltu.moviedb.movienavigator

import android.app.Application
import com.ltu.moviedb.movienavigator.database.AppContainer
import com.ltu.moviedb.movienavigator.database.DefaultAppContainer

class MovieDBApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}