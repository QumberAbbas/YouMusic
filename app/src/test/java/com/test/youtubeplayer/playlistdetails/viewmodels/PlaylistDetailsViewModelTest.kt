package com.test.youtubeplayer.playlistdetails.viewmodels

import com.test.youtubeplayer.BaseTest
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.datasource.FIRST_PAGE_TOKEN
import com.test.youtubeplayer.feature.playlistdetails.viewmodels.PlaylistDetailsViewModel
import com.test.youtubeplayer.repository.YoutubeRepository
import com.test.youtubeplayer.utils.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class PlaylistDetailsViewModelTest : BaseTest() {

    @Mock
    private lateinit var repository: YoutubeRepository

    private lateinit var viewModel: PlaylistDetailsViewModel

    @Before
    fun setup() {
        viewModel = PlaylistDetailsViewModel(repository)
    }

    @Test
    fun `should not fetch playlist videos when next playlist video data is not available`() =
        testCoroutineRule.runBlocking {
            val playlistId = "123"
            `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(false)

            viewModel.fetchPlaylistVideos(playlistId)

            verify(repository, never()).getPlaylists(anyString())
            assertTrue(viewModel.showProgress().value?.not())
            assertNull(viewModel.status().value)
            assertTrue(viewModel.showPlayAll().value?.not())
        }

    @Test
    fun `should fetch playlist videos when requested`() = testCoroutineRule.runBlocking {
        val playlistId = "123"
        val mockVideo = mock(Video::class.java)
        val videoResult = VideosResult(mutableListOf<Video>().apply { add(mockVideo) }, "next")
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(true)
        `when`(repository.getPlaylistVideos(playlistId, FIRST_PAGE_TOKEN)).thenReturn(videoResult)

        viewModel.fetchPlaylistVideos(playlistId)

        assertTrue(viewModel.showProgress().value?.not())
        assertTrue(viewModel.showPlayAll().value)
        val playListVideos = viewModel.listModels().value
        assertNotNull(playListVideos)
        assertTrue(playListVideos?.size?.equals(1))
        assertTrue(playListVideos?.get(0)?.equals(mockVideo))
    }

    @Test
    fun `should not show play all when playlist videos are empty`() = testCoroutineRule.runBlocking {
        val playlistId = "123"
        val videoResult = VideosResult(mutableListOf<Video>().apply { }, "next")
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(true)
        `when`(repository.getPlaylistVideos(playlistId, FIRST_PAGE_TOKEN)).thenReturn(videoResult)

        viewModel.fetchPlaylistVideos(playlistId)

        assertTrue(viewModel.showPlayAll().value?.not())
    }
}