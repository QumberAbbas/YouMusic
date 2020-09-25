package com.test.youtubeplayer.datasource

import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.feature.playlist.models.VideoInfoResult
import com.test.youtubeplayer.network.CacheRetrievalPolicy

const val FIRST_PAGE_TOKEN = "!!FIRST_PAGE!!"

interface DataSource {
    suspend fun getPlaylists(pageToken: String = FIRST_PAGE_TOKEN): PlaylistsResult?
    suspend fun getPlaylistVideos(playlistId: String, pageToken: String = FIRST_PAGE_TOKEN, cacheRetrievalPolicy: CacheRetrievalPolicy = CacheRetrievalPolicy.NETWORK_FIRST): VideosResult?
    suspend fun getVideosFor(searchTerm: String, pageToken: String = FIRST_PAGE_TOKEN): VideosResult
    suspend fun isNextPlaylistDataAvailable(pageToken: String = FIRST_PAGE_TOKEN) : Boolean
    suspend fun getVideosInfo(videoIds: List<String>): Map<String, VideoInfoResult>
    suspend fun isChannelIdAvailable() : Boolean
}