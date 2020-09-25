package com.test.youtubeplayer.feature.player

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.viewmodel.BaseViewModel

class SharedPlayerViewModel @ViewModelInject constructor() : BaseViewModel() {

    private val currentVideo = MutableLiveData<Video>()
    private val endedVideo = MutableLiveData<Video>()

    fun playVideo(video: Video) = currentVideo.postValue(video)
    fun onVideoEnded(video: Video) = endedVideo.postValue(video)
    fun currentVideo(): LiveData<Video> = currentVideo
    fun endedVideo(): LiveData<Video> = endedVideo
}