package com.test.youtubeplayer.feature.search.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.base.viewmodel.InfiniteScrollableViewModel
import com.test.youtubeplayer.repository.YoutubeRepository

class SearchViewModel @ViewModelInject constructor(private val repository: YoutubeRepository) :
    InfiniteScrollableViewModel<VideosResult, Video>(repository) {
    fun fetchVideosFor(searchTerm: String) {
        fetch {
            repository.getVideosFor(searchTerm, nextPageToken)
        }
    }
}