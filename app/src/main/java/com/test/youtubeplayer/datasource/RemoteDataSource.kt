package com.test.youtubeplayer.datasource

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.feature.playlist.models.Playlist
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.feature.playlist.models.VideoInfoResult
import com.test.youtubeplayer.network.CacheRetrievalPolicy
import com.test.youtubeplayer.repository.GOOGLE_SIGN_IN_YOUTUBE_SCOPE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

private const val MAX_RESULTS = 15L

class RemoteDataSource @Inject constructor(@ApplicationContext context: Context) : DataSource {
    private val youTube: YouTube

    init {
        val credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(
            GOOGLE_SIGN_IN_YOUTUBE_SCOPE
        ))
        credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(context)?.account
        youTube = YouTube.Builder(NetHttpTransport(), JacksonFactory(), credential).setApplicationName("YTDemoApp").build()
    }

     override suspend fun isChannelIdAvailable() : Boolean {
         var hasChannel = false
         coroutineScope {
            withContext(Dispatchers.IO) {
                val task = youTube.channels()?.list("snippet")?.apply {
                    mine = true
                }
                val result = task?.execute()

                if (result?.items?.size ?: 0 > 0) {
                    hasChannel = true
                }
            }
         }
         return hasChannel
    }

    override suspend fun getPlaylists(pageToken: String): PlaylistsResult? {
        val playLists = arrayListOf<Playlist>()
        var nextPageToken = ""
        coroutineScope {
            withContext(Dispatchers.IO) {
                val task = youTube.playlists()?.list("snippet,contentDetails")
                task?.mine = true
                task?.maxResults = MAX_RESULTS
                task?.pageToken = pageToken
                task?.userIp

                val result = task?.execute()

                nextPageToken = result?.nextPageToken ?: ""
                result?.items?.forEach { playLists.add(Playlist(it)) }
            }
        }
        return PlaylistsResult(playLists, nextPageToken)
    }

    override suspend fun getPlaylistVideos(playlistId: String, pageToken: String, cacheRetrievalPolicy: CacheRetrievalPolicy): VideosResult? {
        val videos = arrayListOf<Video>()
        var nextPageToken = ""
        coroutineScope {
            withContext(Dispatchers.IO) {
                val task = youTube.playlistItems()?.list("snippet")
                task?.playlistId = playlistId
                task?.maxResults = MAX_RESULTS
                task?.pageToken = pageToken

                val result = task?.execute()

                nextPageToken = result?.nextPageToken ?: ""
                result?.items?.forEach { playlistItem -> videos.add(Video(playlistItem)) }
            }
        }
        return VideosResult(videos, nextPageToken)
    }

    override suspend fun getVideosFor(searchTerm: String, pageToken: String): VideosResult {
        val videos = arrayListOf<Video>()
        var nextPageToken = ""
        coroutineScope {
            withContext(Dispatchers.IO) {
                val task = youTube.search()?.list("snippet")
                task?.q = searchTerm
                task?.maxResults = MAX_RESULTS
                pageToken?.let { task?.pageToken = it }

                val result = task?.execute()

                nextPageToken = result?.nextPageToken ?: ""
                result?.items?.forEach { playlistItem -> if(playlistItem.id.videoId != null) videos.add(Video(playlistItem)) }
            }
        }
        return VideosResult(videos, nextPageToken)
    }

    override suspend fun isNextPlaylistDataAvailable(pageToken: String) = pageToken.isEmpty().not()

    override suspend fun getVideosInfo(videoIds: List<String>): Map<String, VideoInfoResult> {
        val videoInfoResultList = mutableMapOf<String, VideoInfoResult>()
        coroutineScope {
            withContext(Dispatchers.IO) {
                val task = youTube.videos()?.list("contentDetails")
                task?.id = videoIds.joinToString(",")
                val result = task?.execute()
                result?.items?.forEach { video -> videoInfoResultList[video.id] = VideoInfoResult(video) }
            }
        }
        return videoInfoResultList
    }

}