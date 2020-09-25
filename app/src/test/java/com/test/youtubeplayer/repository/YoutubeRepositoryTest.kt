package com.test.youtubeplayer.repository

import com.test.youtubeplayer.BaseTest
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.datasource.FIRST_PAGE_TOKEN
import com.test.youtubeplayer.datasource.LocalDataSource
import com.test.youtubeplayer.datasource.RemoteDataSource
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.feature.playlist.models.VideoInfoResult
import com.test.youtubeplayer.network.CacheRetrievalPolicy
import com.test.youtubeplayer.network.NetworkState
import com.test.youtubeplayer.utils.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class YoutubeRepositoryTest : BaseTest() {

    @Mock
    lateinit var localDataSource: LocalDataSource

    @Mock
    lateinit var remoteDataSource: RemoteDataSource

    @Mock
    lateinit var networkState: NetworkState

    @InjectMocks
    private lateinit var repository: YoutubeRepository

    @Test
    fun `should return cached playlist when offline`() = testCoroutineRule.runBlocking {
        val pageToken = "pageToken"
        val mockPlaylistResult = mock(PlaylistsResult::class.java)
        `when`(networkState.isConnected).thenReturn(false)
        `when`(localDataSource.getPlaylists(pageToken)).thenReturn(mockPlaylistResult)

        val playlistsResult = repository.getPlaylists(pageToken)

        assertEquals(playlistsResult, mockPlaylistResult)
        verify(localDataSource).getPlaylists(pageToken)
        verifyNoInteractions(remoteDataSource)
        verifyNoMoreInteractions(localDataSource)
    }

    @Test
    fun `should return null when online but there are no playlists`() =
        testCoroutineRule.runBlocking {
            val pageToken = "pageToken"
            `when`(networkState.isConnected).thenReturn(true)
            `when`(remoteDataSource.getPlaylists(pageToken)).thenReturn(null)

            val playlistsResult = repository.getPlaylists(pageToken)

            assertNull(playlistsResult)
            verify(remoteDataSource).getPlaylists(pageToken)
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoInteractions(localDataSource)
        }

    @Test
    fun `should return remote playlists after deleting existing cache and updating cache when online on the first page`() =
        testCoroutineRule.runBlocking {
            val pageToken = FIRST_PAGE_TOKEN
            val mockPlaylistResult = mock(PlaylistsResult::class.java)
            `when`(networkState.isConnected).thenReturn(true)
            `when`(remoteDataSource.getPlaylists(pageToken)).thenReturn(mockPlaylistResult)

            val playlistsResult = repository.getPlaylists(pageToken)

            assertEquals(playlistsResult, mockPlaylistResult)
            verify(remoteDataSource).getPlaylists(pageToken)
            verify(localDataSource).deleteAllPlaylistResults()
            verify(localDataSource).addPlaylistResult(mockPlaylistResult, pageToken)
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
        }

    @Test
    fun `should return remote playlists after updating cache for token when online and not on the first page`() =
        testCoroutineRule.runBlocking {
            val pageToken = "NEXT_PAGE"
            val mockPlaylistResult = mock(PlaylistsResult::class.java)
            `when`(networkState.isConnected).thenReturn(true)
            `when`(remoteDataSource.getPlaylists(pageToken)).thenReturn(mockPlaylistResult)

            val playlistsResult = repository.getPlaylists(pageToken)

            assertEquals(playlistsResult, mockPlaylistResult)
            verify(remoteDataSource).getPlaylists(pageToken)
            verify(localDataSource).deletePlaylistResults(pageToken)
            verify(localDataSource).addPlaylistResult(mockPlaylistResult, pageToken)
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
        }

    @Test
    fun `should return null when offline and there are no cached playlist videos`() =
        testCoroutineRule.runBlocking {
            val playlistId = "id1"
            val pageToken = "pageToken"
            val cacheRetrievalPolicy = CacheRetrievalPolicy.NETWORK_FIRST
            `when`(networkState.isConnected).thenReturn(false)
            `when`(
                localDataSource.getPlaylistVideos(
                    playlistId,
                    pageToken,
                    cacheRetrievalPolicy
                )
            ).thenReturn(null)

            val playlistVideosResult =
                repository.getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)

            assertNull(playlistVideosResult)
            verify(localDataSource).getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
            verifyNoInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
        }

    @Test
    fun `should return enhanced cached playlist videos when cache first and there are cached playlist videos`() =
        testCoroutineRule.runBlocking {
            val playlistId = "id1"
            val pageToken = "pageToken"
            val cacheRetrievalPolicy = CacheRetrievalPolicy.CACHE_FIRST
            val videoId1 = "id1"
            val videoId2 = "id2"
            val video1 = Video(videoId1, null, null, null, null)
            val video2 = Video(videoId2, null, null, null, null)
            val duration1 = "12:34"
            val duration2 = "10:10"
            val mockVideosResult = VideosResult(listOf(video1, video2), pageToken)
            val mockVideoInfoResult1 = mock(VideoInfoResult::class.java)
            val mockVideoInfoResult2 = mock(VideoInfoResult::class.java)
            `when`(mockVideoInfoResult1.duration).thenReturn(duration1)
            `when`(mockVideoInfoResult2.duration).thenReturn(duration2)
            `when`(networkState.isConnected).thenReturn(true)
            `when`(
                localDataSource.getPlaylistVideos(
                    playlistId,
                    pageToken,
                    cacheRetrievalPolicy
                )
            ).thenReturn(mockVideosResult)
            `when`(localDataSource.getVideosInfo(listOf(videoId1, videoId2))).thenReturn(
                mapOf(
                    Pair(videoId1, mockVideoInfoResult1), Pair(videoId2, mockVideoInfoResult2)
                )
            )

            val playlistVideosResult =
                repository.getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)

            assertTrue(playlistVideosResult?.listModels?.size == 2)
            assertTrue(playlistVideosResult?.listModels?.get(0)?.duration == duration1)
            assertTrue(playlistVideosResult?.listModels?.get(1)?.duration == duration2)
            verify(localDataSource).getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
            verify(localDataSource).getVideosInfo(listOf(videoId1, videoId2))
            verifyNoInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
        }

    @Test
    fun `should return null when online but not cache first and remote playlist videos is not present`() =
        testCoroutineRule.runBlocking {
            val playlistId = "id1"
            val pageToken = "pageToken"
            val cacheRetrievalPolicy = CacheRetrievalPolicy.NETWORK_FIRST
            `when`(networkState.isConnected).thenReturn(true)
            `when`(
                localDataSource.getPlaylistVideos(
                    playlistId,
                    pageToken,
                    cacheRetrievalPolicy
                )
            ).thenReturn(null)
            `when`(
                localDataSource.getPlaylistVideos(
                    playlistId,
                    pageToken,
                    cacheRetrievalPolicy
                )
            ).thenReturn(null)

            val playlistVideosResult =
                repository.getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)

            assertNull(playlistVideosResult)
            verify(localDataSource).getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
            verify(remoteDataSource).getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
        }

    @Test
    fun `should return remote playlist videos with enhanced duration info from cache when online and not cache first`() =
        testCoroutineRule.runBlocking {
            val playlistId = "id1"
            val pageToken = FIRST_PAGE_TOKEN
            val cacheRetrievalPolicy = CacheRetrievalPolicy.NETWORK_FIRST
            val videoId1 = "id1"
            val videoId2 = "id2"
            val video1 = Video(videoId1, null, null, null, null)
            val video2 = Video(videoId2, null, null, null, null)
            val duration1 = "12:34"
            val duration2 = "10:10"
            val mockVideosResult = VideosResult(listOf(video1, video2), pageToken)
            val mockVideoInfoResult1 = mock(VideoInfoResult::class.java)
            val mockVideoInfoResult2 = mock(VideoInfoResult::class.java)
            `when`(mockVideoInfoResult1.duration).thenReturn(duration1)
            `when`(mockVideoInfoResult2.duration).thenReturn(duration2)
            `when`(networkState.isConnected).thenReturn(true)
            `when`(
                localDataSource.getPlaylistVideos(
                    playlistId,
                    pageToken,
                    cacheRetrievalPolicy
                )
            ).thenReturn(null)
            `when`(
                remoteDataSource.getPlaylistVideos(
                    playlistId,
                    pageToken,
                    cacheRetrievalPolicy
                )
            ).thenReturn(mockVideosResult)
            `when`(localDataSource.getVideosInfo(listOf(videoId1, videoId2))).thenReturn(
                mapOf(
                    Pair(videoId1, mockVideoInfoResult1)
                )
            )
            `when`(remoteDataSource.getVideosInfo(listOf(videoId2))).thenReturn(
                mapOf(
                    Pair(videoId2, mockVideoInfoResult2)
                )
            )

            val playlistVideosResult =
                repository.getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)

            assertTrue(playlistVideosResult?.listModels?.size == 2)
            assertTrue(playlistVideosResult?.listModels?.get(0)?.duration == duration1)
            assertTrue(playlistVideosResult?.listModels?.get(1)?.duration == duration2)
            verify(localDataSource).getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
            verify(remoteDataSource).getPlaylistVideos(playlistId, pageToken, cacheRetrievalPolicy)
            verify(localDataSource).deletePlaylistVideosResult(playlistId)
            verify(localDataSource).addPlaylistVideosResult(playlistId, mockVideosResult, pageToken)
            verify(localDataSource).getVideosInfo(listOf(videoId1, videoId2))
            verify(remoteDataSource).getVideosInfo(listOf(videoId2))
            verify(localDataSource).addVideoInfos(listOf(mockVideoInfoResult2))
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
        }

    @Test
    fun `should return remote videos with enhanced duration info from cache if available`() =
        testCoroutineRule.runBlocking {
            val searchTerm = "searchTerm"
            val pageToken = FIRST_PAGE_TOKEN
            val videoId1 = "id1"
            val videoId2 = "id2"
            val video1 = Video(videoId1, null, null, null, null)
            val video2 = Video(videoId2, null, null, null, null)
            val duration1 = "12:34"
            val duration2 = "10:10"
            val mockVideoInfoResult1 = mock(VideoInfoResult::class.java)
            val mockVideoInfoResult2 = mock(VideoInfoResult::class.java)
            val mockVideosResult = VideosResult(listOf(video1, video2), pageToken)
            `when`(mockVideoInfoResult1.duration).thenReturn(duration1)
            `when`(mockVideoInfoResult2.duration).thenReturn(duration2)
            `when`(
                remoteDataSource.getVideosFor(
                    searchTerm,
                    pageToken
                )
            ).thenReturn(mockVideosResult)
            `when`(localDataSource.getVideosInfo(listOf(videoId1, videoId2))).thenReturn(
                mapOf(
                    Pair(videoId1, mockVideoInfoResult1), Pair(videoId2, mockVideoInfoResult2)
                )
            )

            val videosResult = repository.getVideosFor(searchTerm, pageToken)

            verify(remoteDataSource).getVideosFor(searchTerm, pageToken)
            verify(localDataSource).getVideosInfo(listOf(videoId1, videoId2))
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
            assertTrue(videosResult.listModels.size == 2)
            assertTrue(videosResult.listModels[0].duration == duration1)
            assertTrue(videosResult.listModels[1].duration == duration2)
        }

    @Test
    fun `should return remote videos with enhanced duration info from cache if available and rest from remote after updating cache`() =
        testCoroutineRule.runBlocking {
            val searchTerm = "searchTerm"
            val pageToken = FIRST_PAGE_TOKEN
            val videoId1 = "id1"
            val videoId2 = "id2"
            val video1 = Video(videoId1, null, null, null, null)
            val video2 = Video(videoId2, null, null, null, null)
            val duration1 = "12:34"
            val duration2 = "10:10"
            val mockVideoInfoResult1 = mock(VideoInfoResult::class.java)
            val mockVideoInfoResult2 = mock(VideoInfoResult::class.java)
            val mockVideosResult = VideosResult(listOf(video1, video2), pageToken)
            `when`(mockVideoInfoResult1.duration).thenReturn(duration1)
            `when`(mockVideoInfoResult2.duration).thenReturn(duration2)
            `when`(
                remoteDataSource.getVideosFor(
                    searchTerm,
                    pageToken
                )
            ).thenReturn(mockVideosResult)
            `when`(localDataSource.getVideosInfo(listOf(videoId1, videoId2))).thenReturn(
                mapOf(
                    Pair(videoId1, mockVideoInfoResult1)
                )
            )
            `when`(remoteDataSource.getVideosInfo(listOf(videoId2))).thenReturn(
                mapOf(
                    Pair(videoId2, mockVideoInfoResult2)
                )
            )

            val videosResult = repository.getVideosFor(searchTerm, pageToken)

            verify(remoteDataSource).getVideosFor(searchTerm, pageToken)
            verify(localDataSource).getVideosInfo(listOf(videoId1, videoId2))
            verify(remoteDataSource).getVideosInfo(listOf(videoId2))
            verify(localDataSource).addVideoInfos(listOf(mockVideoInfoResult2))
            verifyNoMoreInteractions(remoteDataSource)
            verifyNoMoreInteractions(localDataSource)
            assertTrue(videosResult.listModels.size == 2)
            assertTrue(videosResult.listModels[0].duration == duration1)
            assertTrue(videosResult.listModels[1].duration == duration2)
        }
}