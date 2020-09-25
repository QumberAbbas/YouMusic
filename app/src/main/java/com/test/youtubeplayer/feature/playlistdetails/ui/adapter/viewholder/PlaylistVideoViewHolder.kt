package com.test.youtubeplayer.feature.playlistdetails.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.databinding.VideoPlaylistItemBinding
import com.test.youtubeplayer.feature.playlistdetails.ui.adapter.VideosAdapter

class PlaylistVideoViewHolder(
    private val binding: VideoPlaylistItemBinding,
    private val onVideoClickListener: VideosAdapter.OnVideoClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlistVideo: Video) {
        binding.video = playlistVideo
        binding.cvPlaylist.setOnClickListener {
            onVideoClickListener?.let { onVideoClickListener.onVideoClicked(playlistVideo) }
        }
    }
}

  