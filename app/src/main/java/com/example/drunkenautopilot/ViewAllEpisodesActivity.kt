package com.example.drunkenautopilot

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.allEpisodesRecyclerView.EpisodeAdapter
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.viewModels.EpisodeViewModel

class ViewAllEpisodesActivity: AppCompatActivity() {

    private lateinit var rvEpisodes: RecyclerView
    private lateinit var episodeViewModel: EpisodeViewModel
    private lateinit var tvErrorMsg: TextView
    var episodes: MutableList<Episode> = mutableListOf()
    set(value) {
        field = value
        rvEpisodes.adapter = EpisodeAdapter(this, field) {
            val intent = Intent(this, ViewEpisodeActivity::class.java)
            intent.putExtra("episode_id", it.id)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_all_episodes_activity)

        rvEpisodes = findViewById(R.id.rv_episodes)
        val layoutManager = LinearLayoutManager(this)
        rvEpisodes.layoutManager = layoutManager
        val decoration = DividerItemDecoration(this, layoutManager.orientation)
        rvEpisodes.addItemDecoration(decoration)

        tvErrorMsg = findViewById(R.id.tv_error_message)

        episodeViewModel = ViewModelProvider(this).get(EpisodeViewModel::class.java)
        episodeViewModel.allEpisodes.observe(this, Observer { allEpisodes ->
            if (allEpisodes.isEmpty()) {
                tvErrorMsg.visibility = TextView.VISIBLE
            } else {
                tvErrorMsg.visibility = TextView.INVISIBLE
            }
            episodes = allEpisodes.toMutableList()
        })
    }
}