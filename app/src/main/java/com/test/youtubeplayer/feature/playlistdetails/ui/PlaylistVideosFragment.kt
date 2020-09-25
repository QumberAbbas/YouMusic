package com.test.youtubeplayer.feature.playlistdetails.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.test.youtubeplayer.base.fragment.VideosFragment
import com.test.youtubeplayer.feature.player.VideoPlayerActivity
import com.test.youtubeplayer.feature.playlistdetails.viewmodels.PlaylistDetailsViewModel
import com.test.youtubeplayer.network.CacheRetrievalPolicy
import dagger.hilt.android.AndroidEntryPoint

private const val KEY_PLAYLIST_ID = "playlistId"
@AndroidEntryPoint
class PlaylistVideosFragment : VideosFragment<PlaylistDetailsViewModel>() {
    private val playListViewModel: PlaylistDetailsViewModel by viewModels()

    override fun getViewModelInstance(): PlaylistDetailsViewModel {
        return playListViewModel
    }

    override fun initialize() {
        super.initialize()
        binding.fabPlayAll.setOnClickListener {
            val videos = viewModel.listModels().value
            val playlistId = arguments?.getString(KEY_PLAYLIST_ID)
            if(videos?.get(0) != null && playlistId != null) {
                val intent = VideoPlayerActivity.create(requireContext(), playlistId, videos[0])
                context?.startActivity(intent)
            }
        }
    }

    override fun getVideos() {
        val playlistId = arguments?.getString(KEY_PLAYLIST_ID)
        playlistId?.let {
            viewModel.fetchPlaylistVideos(it, getCacheRetrievalPolicy())
        }
    }

    private fun getCacheRetrievalPolicy(): CacheRetrievalPolicy {
        if (activity is VideoPlayerActivity) {
            return CacheRetrievalPolicy.CACHE_FIRST
        }
        return CacheRetrievalPolicy.NETWORK_FIRST
    }

    companion object {
        fun create(playlistId: String): VideosFragment<PlaylistDetailsViewModel> {
            return PlaylistVideosFragment()
                .apply {
                    val bundle = Bundle().apply {
                        putString(KEY_PLAYLIST_ID, playlistId)
                    }
                    arguments = bundle
                }
        }
    }

}
