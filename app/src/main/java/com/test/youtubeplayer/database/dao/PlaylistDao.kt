package com.test.youtubeplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.youtubeplayer.database.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist WHERE page_token = :pageToken")
    suspend fun find(pageToken: String): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM playlist WHERE page_token = :pageToken")
    suspend fun delete(pageToken: String)

    @Query("DELETE FROM playlist")
    suspend fun deleteAll()
}