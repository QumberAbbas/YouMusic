package com.test.youtubeplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.youtubeplayer.database.entity.VideoInfoEntity

@Dao
interface VideoInfoDao {
    @Query("SELECT * FROM video_info")
    suspend fun getAll(): List<VideoInfoEntity>

    @Query("SELECT * FROM video_info WHERE video_id = :videoId")
    suspend fun find(videoId: String): VideoInfoEntity?

    @Query("SELECT * FROM video_info WHERE video_id IN(:videoIds)")
    suspend fun find(videoIds: List<String>): List<VideoInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(videoInfoEntity: VideoInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videoInfoEntity: List<VideoInfoEntity>)
}