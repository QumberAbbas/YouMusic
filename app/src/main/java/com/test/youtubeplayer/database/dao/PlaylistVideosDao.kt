package com.test.youtubeplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.youtubeplayer.database.entity.PlaylistVideosEntity

@Dao
interface PlaylistVideosDao {
    @Query("SELECT * FROM playlist_videos WHERE page_token = :pageToken AND playlist_id = :playlistId")
    suspend fun find(playlistId: String, pageToken: String): PlaylistVideosEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlistEntity: PlaylistVideosEntity)

    @Query("DELETE FROM playlist_videos WHERE playlist_id = :playlistId")
    suspend fun delete(playlistId: String)

    @Query("DELETE FROM playlist_videos WHERE playlist_id = :playlistId AND page_token = :pageToken")
    suspend fun delete(playlistId: String, pageToken: String)
}