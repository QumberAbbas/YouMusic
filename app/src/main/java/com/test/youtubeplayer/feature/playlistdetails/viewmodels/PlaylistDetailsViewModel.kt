package com.test.youtubeplayer.feature.playlistdetails.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.base.viewmodel.InfiniteScrollableViewModel
import com.test.youtubeplayer.network.CacheRetrievalPolicy
import com.test.youtubeplayer.repository.YoutubeRepository

class PlaylistDetailsViewModel @ViewModelInject constructor(private val repository: YoutubeRepository) :
    InfiniteScrollableViewModel<VideosResult, Video>(repository) {

    fun fetchPlaylistVideos(
        playListId: String,
        cacheRetrievalPolicy: CacheRetrievalPolicy = CacheRetrievalPolicy.NETWORK_FIRST
    ) {
        fetch {
            repository.getPlaylistVideos(playListId, nextPageToken, cacheRetrievalPolicy)
        }
    }
}
