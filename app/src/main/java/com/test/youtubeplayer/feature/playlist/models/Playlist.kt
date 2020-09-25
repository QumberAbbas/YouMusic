package com.test.youtubeplayer.feature.playlist.models

import android.os.Parcelable
import com.google.api.services.youtube.model.Playlist
import com.test.youtubeplayer.base.models.ListModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Playlist(
    val id: String,
    val title: String?,
    val videosCount: Long,
    val icon: String?
) : Parcelable, ListModel {
    constructor(playlist: Playlist) : this(
        playlist.id,
        playlist.snippet?.title,
        playlist.contentDetails?.itemCount ?: 0,
        playlist.snippet?.thumbnails?.maxres?.url
    )
}