package com.test.youtubeplayer.repository

import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.datasource.DataSource
import com.test.youtubeplayer.datasource.FIRST_PAGE_TOKEN
import com.test.youtubeplayer.datasource.LocalDataSource
import com.test.youtubeplayer.datasource.RemoteDataSource
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.feature.playlist.models.VideoInfoResult
import com.test.youtubeplayer.network.CacheRetrievalPolicy
import com.test.youtubeplayer.network.NetworkState
import javax.inject.Inject

const val GOOGLE_SIGN_IN_YOUTUBE_SCOPE = "https://www.googleapis.com/auth/youtube"

class YoutubeRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val networkState: NetworkState
) : DataSource {

    override suspend fun getPlaylists(pageToken: String): PlaylistsResult? {
        if (networkState.isConnected.not()) return localDataSource.getPlaylists(pageToken)
        val playlistsResult = remoteDataSource.getPlaylists(pageToken) ?: return null
        updatePlaylistResultsCache(pageToken, playlistsResult)
        return playlistsResult
    }

    override suspend fun getPlaylistVideos(
        playlistId: String,
        pageToken: String,
        cacheRetrievalPolicy: CacheRetrievalPolicy
    ): VideosResult? {
        val localPlaylistVideosResult =
            localDataSource.getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
        if (networkState.isConnected.not() || (cacheRetrievalPolicy == CacheRetrievalPolicy.CACHE_FIRST && localPlaylistVideosResult != null))
            return if (localPlaylistVideosResult != null) enhanceVideoResults(
                localPlaylistVideosResult,
                CacheRetrievalPolicy.CACHE_ONLY
            ) else null
        val playlistVideosResult =
            remoteDataSource.getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
                ?: return null
        updatePlaylistVideosResultCache(pageToken, playlistVideosResult, playlistId)
        return enhanceVideoResults(playlistVideosResult)
    }

    override suspend fun getVideosFor(searchTerm: String, pageToken: String): VideosResult {
        val videos = remoteDataSource.getVideosFor(searchTerm, pageToken)
        return enhanceVideoResults(videos)
    }

    override suspend fun isNextPlaylistDataAvailable(pageToken: String): Boolean {
        return getActiveDataSource().isNextPlaylistDataAvailable(pageToken)
    }

    private suspend fun updatePlaylistResultsCache(
        pageToken: String,
        playlistsResult: PlaylistsResult
    ) {
        if (isFirstPageCall(pageToken)) localDataSource.deleteAllPlaylistResults()
        else localDataSource.deletePlaylistResults(pageToken)
        localDataSource.addPlaylistResult(playlistsResult, pageToken)
    }

    private suspend fun updatePlaylistVideosResultCache(
        pageToken: String,
        videosResult: VideosResult,
        playlistId: String
    ) {
        if (isFirstPageCall(pageToken)) localDataSource.deletePlaylistVideosResult(playlistId)
        else localDataSource.deletePlaylistVideosResults(playlistId, pageToken)
        localDataSource.addPlaylistVideosResult(playlistId, videosResult, pageToken)
    }

    private fun isFirstPageCall(pageToken: String) = pageToken == FIRST_PAGE_TOKEN

    private suspend fun enhanceVideoResults(
        videoResults: VideosResult,
        cacheRetrievalPolicy: CacheRetrievalPolicy = CacheRetrievalPolicy.CACHE_FIRST
    ): VideosResult {
        val videoIds = videoResults.listModels.map { m -> m.videoId }
        val allVideoInfos = mutableMapOf<String, VideoInfoResult>()
        val cachedVideoInfos = localDataSource.getVideosInfo(videoIds)
        allVideoInfos.putAll(cachedVideoInfos)
        val remoteVideoIds = videoIds.filter { id -> cachedVideoInfos.keys.contains(id).not() }
        if (cacheRetrievalPolicy != CacheRetrievalPolicy.CACHE_ONLY && remoteVideoIds.isNotEmpty()) {
            val remoteVideoInfos = remoteDataSource.getVideosInfo(remoteVideoIds)
            localDataSource.addVideoInfos(remoteVideoInfos.values.toList())
            allVideoInfos.putAll(remoteVideoInfos)
        }
        videoResults.listModels.forEach { video ->
            video.duration = allVideoInfos[video.videoId]?.duration
        }
        return videoResults
    }

    override suspend fun getVideosInfo(videoIds: List<String>): Map<String, VideoInfoResult> {
        TODO("Not a valid operation")
    }

    private fun getActiveDataSource(): DataSource {
        return if (networkState.isConnected) remoteDataSource else localDataSource
    }

    override suspend fun isChannelIdAvailable(): Boolean {
        if ((networkState.isConnected.not() && localDataSource.isChannelIdAvailable()) || remoteDataSource.isChannelIdAvailable()) {
            localDataSource.cacheChannelIdAvailability(true)
            return true
        }
        return false
    }
}