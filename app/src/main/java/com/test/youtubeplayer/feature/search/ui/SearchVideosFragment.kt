package com.test.youtubeplayer.feature.search.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.test.youtubeplayer.base.fragment.VideosFragment
import com.test.youtubeplayer.feature.player.VideoPlayerActivity
import com.test.youtubeplayer.feature.search.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val KEY_QUERY_TERM = "queryTerm"

@AndroidEntryPoint
class SearchVideosFragment : VideosFragment<SearchViewModel>() {

    private val searViewModel: SearchViewModel by viewModels()

    override fun getViewModelInstance(): SearchViewModel {
        return searViewModel
    }

    companion object {
        fun create(queryTerm: String): VideosFragment<SearchViewModel> {
            return SearchVideosFragment()
                .apply {
                    val bundle = Bundle().apply {
                        putString(KEY_QUERY_TERM, queryTerm)
                    }
                    arguments = bundle
                }
        }
    }

    override fun initialize() {
        super.initialize()
        binding.fabPlayAll.setOnClickListener {
            val videos = viewModel.listModels().value
            val queryTerm = arguments?.getString(KEY_QUERY_TERM)
            if (videos?.get(0) != null && queryTerm != null) {
                val intent = VideoPlayerActivity.createWithSearchQuery(
                    requireContext(),
                    queryTerm,
                    videos[0]
                )
                context?.startActivity(intent)
            }
        }
    }

    override fun getVideos() {
        val playlistId = arguments?.getString(KEY_QUERY_TERM)
        playlistId?.let {
            viewModel.fetchVideosFor(it)
        }
    }


}