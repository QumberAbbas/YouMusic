package com.test.youtubeplayer.feature.player

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.activity.BaseActivity
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.databinding.ActivityYoutubePlayerScreenBinding
import com.test.youtubeplayer.feature.playlistdetails.ui.PlaylistVideosFragment
import com.test.youtubeplayer.feature.search.ui.SearchVideosFragment
import dagger.hilt.android.AndroidEntryPoint

private const val KEY_PLAYLIST_ID = "playlist_id"
private const val KEY_PLAYLIST_VIDEO = "playlistVideo"
private const val KEY_QUERY_TERM = "queryTerm"

@AndroidEntryPoint
class VideoPlayerActivity : BaseActivity<ActivityYoutubePlayerScreenBinding>() {

    private lateinit var player : YouTubePlayer
    private var currentVideo : Video? = null

    private val sharedPlayerViewModel: SharedPlayerViewModel by viewModels()

    override fun getLayoutRes(): Int {
        return R.layout.activity_youtube_player_screen
    }

    override fun initialize() {
        currentVideo = intent.getParcelableExtra(KEY_PLAYLIST_VIDEO)
        val playlistId = intent.getStringExtra(KEY_PLAYLIST_ID)
        val searchQuery = intent.getStringExtra(KEY_QUERY_TERM)

        lifecycle.addObserver(binding.youtubePlayerView)

        sharedPlayerViewModel.currentVideo().observe(this, Observer {
            currentVideo = it
            playVideo(it)
        })
        currentVideo?.let {
            // In offline, youtube player will not ready, hence set here as well
            updateVideoInfo(it)
            playInitialVideo(it)

            if(playlistId != null) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.flContainer, PlaylistVideosFragment.create(playlistId))
                            .commit()
            }
            if(searchQuery != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.flContainer, SearchVideosFragment.create(searchQuery))
                    .commit()
            }
        }
    }

    private fun updateVideoInfo(video: Video) {
        binding.video = video
    }

    private fun playInitialVideo(video : Video) {
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                player = youTubePlayer
                playVideo(video)
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                if(state == PlayerConstants.PlayerState.ENDED) {
                    currentVideo?.let {
                        sharedPlayerViewModel.onVideoEnded(it)
                    }
                }
            }
        })
    }

    private fun playVideo(video: Video) {
        updateVideoInfo(video)
        player.loadVideo(video.videoId, 0f)
    }

    companion object {
        fun create(context: Context, playlistVideo: Video): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(KEY_PLAYLIST_VIDEO, playlistVideo)
            }
        }

        fun create(context: Context, playlistId: String, initialVideo: Video): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(KEY_PLAYLIST_VIDEO, initialVideo)
                putExtra(KEY_PLAYLIST_ID, playlistId)
            }
        }

        fun createWithSearchQuery(context: Context, searchQuery: String, initialVideo: Video): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(KEY_PLAYLIST_VIDEO, initialVideo)
                putExtra(KEY_QUERY_TERM, searchQuery)
            }
        }
    }
}
