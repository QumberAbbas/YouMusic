package com.test.youtubeplayer.feature.playlist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.youtubeplayer.R
import com.test.youtubeplayer.databinding.RowPlaylistItemBinding
import com.test.youtubeplayer.feature.playlist.models.Playlist
import com.test.youtubeplayer.feature.playlist.ui.adapter.viewholder.PlaylistViewHolder

class PlaylistAdapter(
    private val playlists: MutableList<Playlist>,
    private val onClick: (playList: Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    var onEndReachedListener: OnEndReachedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = DataBindingUtil.inflate<RowPlaylistItemBinding>(
            LayoutInflater.from(parent.context), R.layout.row_playlist_item,
            parent, false
        )
        return PlaylistViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        if (onEndReachedListener != null && holder.adapterPosition == playlists.size - 1) {
            onEndReachedListener?.onRecyclerEndReached(holder.adapterPosition)
        }
    }

    fun updateAll(playLists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(playLists)
        notifyDataSetChanged()
    }

    interface OnEndReachedListener {
        fun onRecyclerEndReached(position: Int)
    }
}
