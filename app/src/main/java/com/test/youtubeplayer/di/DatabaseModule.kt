package com.test.youtubeplayer.di

import android.app.Application
import androidx.room.Room
import com.test.youtubeplayer.database.AppDatabase
import com.test.youtubeplayer.database.dao.PlaylistDao
import com.test.youtubeplayer.database.dao.PlaylistVideosDao
import com.test.youtubeplayer.database.dao.VideoInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideUserDatabase(application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun providePlayListDao(database: AppDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    @Singleton
    fun providePlayListVideosDao(database: AppDatabase): PlaylistVideosDao {
        return database.playlistVideosDao()
    }

    @Provides
    @Singleton
    fun provideVideoInfoDao(database: AppDatabase): VideoInfoDao {
        return database.videoInfoDao()
    }
}

const val DATABASE_NAME = "app_database.db"