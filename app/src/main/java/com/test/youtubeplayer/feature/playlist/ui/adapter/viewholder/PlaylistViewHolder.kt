package com.test.youtubeplayer.feature.playlist.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.test.youtubeplayer.databinding.RowPlaylistItemBinding
import com.test.youtubeplayer.feature.playlist.models.Playlist

class PlaylistViewHolder(
    private val binding: RowPlaylistItemBinding,
    private val onClick: (playList: Playlist) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlist = playlist
        binding.cvPlaylist.setOnClickListener {
            onClick(playlist)
        }
    }
}
