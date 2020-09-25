package com.test.youtubeplayer.feature.playlist.ui

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.activity.BaseActivity
import com.test.youtubeplayer.databinding.ActivityPlayListBinding
import com.test.youtubeplayer.feature.playlist.ui.adapter.PlaylistAdapter
import com.test.youtubeplayer.feature.playlist.viewmodels.PlaylistViewModel
import com.test.youtubeplayer.feature.playlistdetails.ui.PlaylistDetailsActivity
import com.test.youtubeplayer.feature.search.ui.SearchActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlayListActivity : BaseActivity<ActivityPlayListBinding>() {

    private val viewModel: PlaylistViewModel by viewModels()

    private val playlistAdapter by lazy {
        PlaylistAdapter(mutableListOf()) {
            val intent = PlaylistDetailsActivity.create(this@PlayListActivity, it)
            startActivity(intent)
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_play_list
    }

    override fun initialize() {
        addObservers()
        setupRecyclerView()
        viewModel.fetchPlaylist()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playlist_screen_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.searchOption) {
            startActivity(SearchActivity.create(this))
        }
        return true
    }

    private fun setupRecyclerView() {
        playlistAdapter.onEndReachedListener = object : PlaylistAdapter.OnEndReachedListener {
            override fun onRecyclerEndReached(position: Int) {
                viewModel.fetchPlaylist()
            }

        }
        binding.playlistRecyclerView.apply {
            adapter = playlistAdapter
        }
    }

    private fun addObservers() {
        viewModel.showProgress().observe(this,
            Observer {
                it?.let {
                    if (it) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }

            })

        viewModel.status().observe(this, Observer {
            if (it == null) {
                binding.tvStatus.visibility = View.GONE
            } else {
                binding.tvStatus.text = it
                binding.tvStatus.visibility = View.VISIBLE
            }
        })

        viewModel.listModels().observe(this,
            Observer {
                it?.let {
                    playlistAdapter.updateAll(it)
                }
            })

        viewModel.channelIdNotAvailableError().observe(this, Observer {
            it?.let {
                if (it) binding.tvError.visibility = View.VISIBLE else binding.tvError.visibility =
                    View.GONE
            }
        })
    }

    companion object {
        fun create(context: Context): Intent {
            return Intent(context, PlayListActivity::class.java)
        }
    }
}