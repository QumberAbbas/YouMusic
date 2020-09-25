package com.test.youtubeplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.youtubeplayer.database.dao.PlaylistDao
import com.test.youtubeplayer.database.dao.PlaylistVideosDao
import com.test.youtubeplayer.database.dao.VideoInfoDao
import com.test.youtubeplayer.database.entity.PlaylistEntity
import com.test.youtubeplayer.database.entity.PlaylistVideosEntity
import com.test.youtubeplayer.database.entity.VideoInfoEntity

@Database(
    entities = [PlaylistEntity::class, PlaylistVideosEntity::class, VideoInfoEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistVideosDao(): PlaylistVideosDao
    abstract fun videoInfoDao(): VideoInfoDao
}