package com.test.youtubeplayer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_info")
data class VideoInfoEntity(
        @ColumnInfo(name = "video_response") val resultResponse: String,
        @ColumnInfo(name = "video_id") val videoId: String,
        @PrimaryKey(autoGenerate = true) val uid: Int? = null
)