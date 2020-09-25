package com.test.youtubeplayer.di

import com.test.youtubeplayer.network.NetworkState
import com.test.youtubeplayer.network.NetworkStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Provides
    fun provideNetworkStateHolder() : NetworkState {
        return NetworkStateHolder
    }
}
