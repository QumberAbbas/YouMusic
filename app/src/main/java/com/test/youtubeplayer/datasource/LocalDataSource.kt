package com.test.youtubeplayer.datasource

import com.google.gson.Gson
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.database.AppDatabase
import com.test.youtubeplayer.database.entity.PlaylistEntity
import com.test.youtubeplayer.database.entity.PlaylistVideosEntity
import com.test.youtubeplayer.database.entity.VideoInfoEntity
import com.test.youtubeplayer.extensions.toJson
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.feature.playlist.models.VideoInfoResult
import com.test.youtubeplayer.network.CacheRetrievalPolicy
import com.test.youtubeplayer.preferences.AppPrefrences
import javax.inject.Inject


class LocalDataSource @Inject constructor(database: AppDatabase, private val sharedPreferences: AppPrefrences) :
    DataSource {
    private val playlistDao = database.playlistDao()
    private val playlistVideosDao = database.playlistVideosDao()
    private val videoInfoDao = database.videoInfoDao()

    override suspend fun getPlaylists(pageToken: String): PlaylistsResult? {
        val playlistEntity = playlistDao.find(pageToken)
        return if (playlistEntity != null) Gson().fromJson(playlistEntity.resultResponse, PlaylistsResult::class.java) else null
    }

    override suspend fun getPlaylistVideos(playlistId: String, pageToken: String, cacheRetrievalPolicy: CacheRetrievalPolicy): VideosResult? {
        val playlistVideos = playlistVideosDao.find(playlistId, pageToken)
        return if (playlistVideos != null) Gson().fromJson(playlistVideos.resultResponse, VideosResult::class.java) else null
    }

    override suspend fun getVideosFor(searchTerm: String, pageToken: String): VideosResult {
        TODO("Not valid operation")
    }

    override suspend fun isNextPlaylistDataAvailable(pageToken: String) = playlistDao.find(pageToken) != null

    override suspend fun getVideosInfo(videoIds: List<String>): Map<String, VideoInfoResult> {
        val videoEntities = videoInfoDao.find(videoIds)
        return videoEntities.map { e -> Gson().fromJson(e.resultResponse, VideoInfoResult::class.java) }.map { it.videoId to it }.toMap()
    }

    suspend fun addPlaylistResult(playlistResult: PlaylistsResult, pageToken: String) {
        playlistDao.insert(PlaylistEntity(playlistResult.toJson(), pageToken))
    }

    suspend fun deletePlaylistResults(pageToken: String) = playlistDao.delete(pageToken)

    suspend fun deleteAllPlaylistResults() = playlistDao.deleteAll()

    suspend fun addPlaylistVideosResult(playlistId: String, videosResult: VideosResult, pageToken: String) {
        playlistVideosDao.insert(PlaylistVideosEntity(videosResult.toJson(), playlistId, pageToken))
    }

    suspend fun deletePlaylistVideosResult(playlistId: String) = playlistVideosDao.delete(playlistId)

    suspend fun deletePlaylistVideosResults(playlistId: String, pageToken: String) = playlistVideosDao.delete(playlistId, pageToken)

    suspend fun addVideoInfos(videoInfos: List<VideoInfoResult>) {
        val videoInfoEntities = videoInfos.map {videoInfo ->  VideoInfoEntity(videoInfo.toJson(), videoInfo.videoId) }
        videoInfoDao.insertAll(videoInfoEntities)
    }

    fun cacheChannelIdAvailability(isAvailable: Boolean) {
        sharedPreferences.isChannelAvailable = isAvailable
    }

    override suspend fun isChannelIdAvailable() = sharedPreferences.isChannelAvailable
}