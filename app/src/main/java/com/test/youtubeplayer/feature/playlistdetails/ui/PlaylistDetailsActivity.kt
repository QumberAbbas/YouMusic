package com.test.youtubeplayer.feature.playlistdetails.ui

import android.content.Context
import android.content.Intent
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.activity.BaseActivity
import com.test.youtubeplayer.databinding.ActivityPlaylistDetailsScreenBinding
import com.test.youtubeplayer.feature.playlist.models.Playlist
import dagger.hilt.android.AndroidEntryPoint

private const val KEY_PLAYLIST = "playlist"


@AndroidEntryPoint
class PlaylistDetailsActivity : BaseActivity<ActivityPlaylistDetailsScreenBinding>() {

    private var playlist: Playlist? = null

    override fun getLayoutRes(): Int {
        return R.layout.activity_playlist_details_screen
    }

    override fun initialize() {
        playlist = intent.getParcelableExtra(KEY_PLAYLIST)

        playlist?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, PlaylistVideosFragment.create(it.id))
                .commit()
            title = it.title
        }
    }

    companion object {
        fun create(context: Context, playlist: Playlist): Intent {
            return Intent(context, PlaylistDetailsActivity::class.java).apply {
                putExtra(KEY_PLAYLIST, playlist)
            }
        }
    }
}
