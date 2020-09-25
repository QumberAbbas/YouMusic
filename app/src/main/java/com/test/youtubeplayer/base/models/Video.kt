package com.test.youtubeplayer.base.models

import android.os.Parcelable
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.SearchResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val videoId: String,
    val title: String?,
    val icon: String?,
    val author: String?,
    var duration: String?
) : Parcelable, ListModel {
    constructor(playlistItem: PlaylistItem) : this(
        playlistItem.snippet.resourceId.videoId,
        playlistItem.snippet?.title,
        playlistItem.snippet?.thumbnails?.high?.url,
        playlistItem.snippet?.channelTitle,
        null
    )

    constructor(searchResult: SearchResult) : this(
        searchResult.id.videoId,
        searchResult.snippet?.title,
        searchResult.snippet?.thumbnails?.high?.url,
        searchResult.snippet?.channelTitle,
        null
    )
}
