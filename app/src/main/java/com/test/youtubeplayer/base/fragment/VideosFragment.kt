package com.test.youtubeplayer.base.fragment

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.base.viewmodel.InfiniteScrollableViewModel
import com.test.youtubeplayer.databinding.LayoutInfiniteScrollableListBinding
import com.test.youtubeplayer.feature.player.SharedPlayerViewModel
import com.test.youtubeplayer.feature.player.VideoPlayerActivity
import com.test.youtubeplayer.feature.playlistdetails.ui.adapter.VideosAdapter


abstract class VideosFragment<T : InfiniteScrollableViewModel<VideosResult, Video>> : BaseFragment<LayoutInfiniteScrollableListBinding>(), VideosAdapter.OnVideoClickListener {

    lateinit var viewModel: T
    private lateinit var videosAdapter: VideosAdapter

    private val videos = mutableListOf<Video>()

    private val sharedPlayerViewModel: SharedPlayerViewModel by viewModels()

    abstract fun getViewModelInstance() : T

    override fun getLayoutRes(): Int {
        return R.layout.layout_infinite_scrollable_list
    }

    override fun initialize() {
        viewModel = getViewModelInstance()
        addObservers()
        getVideos()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    protected abstract fun getVideos()

    private fun addObservers() {
        viewModel.showProgress().observe(this,
            Observer {

            })

        sharedPlayerViewModel.endedVideo().observe(this, Observer { endedVideo ->
            val videos = viewModel.listModels().value
            if (videos != null && endedVideo != null) {
                val endedVideoIndex = videos.indexOf(endedVideo)
                if (endedVideoIndex != -1 && endedVideoIndex != videos.size - 1) {
                    sharedPlayerViewModel.playVideo(videos[endedVideoIndex + 1])
                }

            }
        })

        viewModel.status().observe(this, Observer {
            if (it == null) {
                binding.tvStatus.visibility = View.GONE
            } else {
                binding.tvStatus.text = it
                binding.tvStatus.visibility = VISIBLE
            }
        })

        viewModel.showPlayAll().observe(this, Observer {
            binding.fabPlayAll.visibility =
                if (it && (activity is VideoPlayerActivity).not()) VISIBLE else GONE
        })

        viewModel.listModels().observe(this,
            Observer {
                it?.let {
                    videos.addAll(it)
                    videosAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun setupRecyclerView() {
        videosAdapter = VideosAdapter(videos)
        videosAdapter.onVideoClickListener = this
        videosAdapter.onEndReachedListener =
            object :
                VideosAdapter.OnEndReachedListener {
                override fun onRecyclerEndReached(position: Int) {
                    getVideos()
                }

            }
        binding.recyclerView.apply {
            adapter = videosAdapter
        }
    }

    override fun onVideoClicked(video: Video) {
        if (activity is VideoPlayerActivity) {
            sharedPlayerViewModel.playVideo(video)
        } else {
            val intent = VideoPlayerActivity.create(requireContext(), video)
            context?.startActivity(intent)
        }
    }
}