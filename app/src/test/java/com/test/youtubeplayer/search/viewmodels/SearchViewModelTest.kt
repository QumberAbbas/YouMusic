package com.test.youtubeplayer.search.viewmodels

import com.test.youtubeplayer.BaseTest
import com.test.youtubeplayer.base.models.Video
import com.test.youtubeplayer.base.models.VideosResult
import com.test.youtubeplayer.datasource.FIRST_PAGE_TOKEN
import com.test.youtubeplayer.feature.search.viewmodels.SearchViewModel
import com.test.youtubeplayer.repository.YoutubeRepository
import com.test.youtubeplayer.utils.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class SearchViewModelTest : BaseTest() {

    @Mock
    private lateinit var repository: YoutubeRepository

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun `should not fetch search videos when next search video data is not available`() = testCoroutineRule.runBlocking {
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(false)

        viewModel.fetchVideosFor("searchTerm")

        verify(repository, never()).getPlaylists(anyString())
        assertTrue(viewModel.showProgress().value?.not())
        Assert.assertNull(viewModel.status().value)
        assertTrue(viewModel.showPlayAll().value?.not())
    }

    @Test
    fun `should fetch search videos for a search term when requested`() = testCoroutineRule.runBlocking {
        val searchTerm = "hello"
        val mockVideo = mock(Video::class.java)
        val videoResult = VideosResult(mutableListOf<Video>().apply { add(mockVideo) }, "next")
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(true)
        `when`(repository.getVideosFor(searchTerm, FIRST_PAGE_TOKEN)).thenReturn(videoResult)

        viewModel.fetchVideosFor(searchTerm)

        assertTrue(viewModel.showProgress().value?.not())
        val searchVideos = viewModel.listModels().value
        assertNotNull(searchVideos)
        assertTrue(searchVideos?.size?.equals(1))
        assertTrue(searchVideos?.get(0)?.equals(mockVideo))
    }

    @Test
    fun `should not show play all when search videos are empty`() = testCoroutineRule.runBlocking {
        val searchTerm = "123"
        val videoResult = VideosResult(mutableListOf<Video>().apply { }, "next")
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(true)
        `when`(repository.getVideosFor(searchTerm, FIRST_PAGE_TOKEN)).thenReturn(videoResult)

        viewModel.fetchVideosFor(searchTerm)

        assertTrue(viewModel.showPlayAll().value?.not())
    }
}