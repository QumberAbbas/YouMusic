package com.test.youtubeplayer.feature.playlist.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.youtubeplayer.base.viewmodel.InfiniteScrollableViewModel
import com.test.youtubeplayer.feature.playlist.models.Playlist
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.repository.YoutubeRepository
import kotlinx.coroutines.launch

class PlaylistViewModel @ViewModelInject constructor(private val repository: YoutubeRepository) :
    InfiniteScrollableViewModel<PlaylistsResult, Playlist>(repository) {

    private val channelIdNotAvailableError = MutableLiveData<Boolean>(false)

    fun fetchPlaylist() {
        viewModelScope.launch {
            if (repository.isChannelIdAvailable()) {
                fetch {
                    repository.getPlaylists(nextPageToken)
                }
            } else {
                channelIdNotAvailableError.postValue(true)
            }
        }
    }

    fun channelIdNotAvailableError(): LiveData<Boolean> = channelIdNotAvailableError
}