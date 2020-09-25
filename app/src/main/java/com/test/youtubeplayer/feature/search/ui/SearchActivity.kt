package com.test.youtubeplayer.feature.search.ui

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.widget.SearchView
import android.widget.Toast
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.activity.BaseActivity
import com.test.youtubeplayer.databinding.ActivitySearchScreenBinding
import com.test.youtubeplayer.network.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchScreenBinding>() {


    override fun getLayoutRes(): Int {
        return R.layout.activity_search_screen
    }

    @Inject
    lateinit var networkState: NetworkState

    override fun initialize() {
        super.initialize()
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_screen_menu, menu)
        val menuItem = menu!!.findItem(R.id.searchOption)
        val searchView = menuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.enter_video_name_to_search)
        searchView.isIconified = false
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addSearchVideoFragment(it)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addSearchVideoFragment(query: String) {
        binding.lblSearch.visibility = GONE
        if (networkState.isConnected) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, SearchVideosFragment.create(query))
                .commit()
        } else {
            Toast.makeText(
                this@SearchActivity,
                getString(R.string.no_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        fun create(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}