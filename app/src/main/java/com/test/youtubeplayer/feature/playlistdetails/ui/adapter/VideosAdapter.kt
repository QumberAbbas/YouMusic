package com.test.youtubeplayer.feature.playlistdetails.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.databinding.VideoPlaylistItemBinding
import com.test.youtubeplayer.feature.playlistdetails.ui.adapter.viewholder.PlaylistVideoViewHolder

class VideosAdapter(private val videos: List<Video>) :
    RecyclerView.Adapter<PlaylistVideoViewHolder>() {

    var onEndReachedListener: OnEndReachedListener? = null
    var onVideoClickListener: OnVideoClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistVideoViewHolder {

        val binding = DataBindingUtil.inflate<VideoPlaylistItemBinding>(
            LayoutInflater.from(parent.context), R.layout.video_playlist_item,
            parent, false
        )
        return PlaylistVideoViewHolder(binding, onVideoClickListener)
    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: PlaylistVideoViewHolder, position: Int) {
        holder.bind(videos[position])
        if (onEndReachedListener != null && holder.adapterPosition == videos.size - 1) {
            onEndReachedListener?.onRecyclerEndReached(holder.adapterPosition)
        }
    }

    interface OnEndReachedListener {
        fun onRecyclerEndReached(position: Int)
    }

    interface OnVideoClickListener {
        fun onVideoClicked(video: Video)
    }
}
