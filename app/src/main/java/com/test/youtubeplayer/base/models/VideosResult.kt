package com.test.youtubeplayer.base.models

class VideosResult(
    override val listModels: List<Video>,
    override val nextPageToken: String
) : ListResult<Video>