package com.test.youtubeplayer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @ColumnInfo(name = "result_response") val resultResponse: String,
    @ColumnInfo(name = "page_token") val pageToken: String,
    @PrimaryKey(autoGenerate = true) val uid: Int? = null
)