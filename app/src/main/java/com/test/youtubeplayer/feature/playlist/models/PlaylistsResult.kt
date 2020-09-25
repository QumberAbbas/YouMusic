package com.test.youtubeplayer.feature.playlist.models

import com.test.youtubeplayer.base.models.ListResult

class PlaylistsResult(
    override val listModels: List<Playlist>,
    override val nextPageToken: String
) : ListResult<Playlist>