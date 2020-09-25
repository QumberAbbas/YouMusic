package com.test.youtubeplayer.playlist.viewmodels

import com.test.youtubeplayer.BaseTest
import com.test.youtubeplayer.datasource.FIRST_PAGE_TOKEN
import com.test.youtubeplayer.feature.playlist.models.Playlist
import com.test.youtubeplayer.feature.playlist.models.PlaylistsResult
import com.test.youtubeplayer.feature.playlist.viewmodels.PlaylistViewModel
import com.test.youtubeplayer.repository.YoutubeRepository
import com.test.youtubeplayer.utils.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class PlaylistViewModelTest : BaseTest() {
    @Mock
    private lateinit var repository: YoutubeRepository

    private lateinit var viewModel: PlaylistViewModel

    @Before
    fun setup() {
        testCoroutineRule.runBlocking {
            viewModel = PlaylistViewModel(repository)
            `when`(repository.isChannelIdAvailable()).thenReturn(true)
        }
    }

    @Test
    fun `should not fetch playlist when next playlist data is not available`() = testCoroutineRule.runBlocking {
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(false)

        viewModel.fetchPlaylist()

        verify(repository, never()).getPlaylists(anyString())
        assertTrue(viewModel.showProgress().value?.not())
        assertNull(viewModel.status().value)
    }

    @Test
    fun `should show progress and fetch playlist when requested for the first page`() = testCoroutineRule.runBlocking {
        val mockPlaylist = mock(Playlist::class.java)
        val playlistResult = PlaylistsResult(mutableListOf<Playlist>().apply { add(mockPlaylist) }, "next")
        `when`(repository.isNextPlaylistDataAvailable(FIRST_PAGE_TOKEN)).thenReturn(true)
        `when`(repository.getPlaylists(FIRST_PAGE_TOKEN)).thenReturn(playlistResult)

        viewModel.fetchPlaylist()

        assertNull(viewModel.status().value)
        val playLists = viewModel.listModels().value
        Assert.assertNotNull(playLists)
        assertTrue(playLists?.size?.equals(1))
        assertTrue(playLists?.get(0)?.equals(mockPlaylist))
    }
}
