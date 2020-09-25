package com.test.youtubeplayer.application

import android.app.Application
import com.test.youtubeplayer.network.NetworkStateHolder.registerConnectivityMonitor
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YoutubePlayerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        registerConnectivityMonitor()
    }
    // Hilt as DI
}