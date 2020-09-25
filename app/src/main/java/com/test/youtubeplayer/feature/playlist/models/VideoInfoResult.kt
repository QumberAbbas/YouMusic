package com.test.youtubeplayer.feature.playlist.models

import com.google.api.services.youtube.model.Video

open class VideoInfoResult constructor(
    val videoId: String,
    _duration: String?
) {
    val duration =
        _duration?.let { Regex("[a-zA-Z]").replace(_duration, " ").trim().replace(" ", ":") }

    constructor(video: Video) : this(video.id, video.contentDetails?.duration)
}