package com.example.drunkenautopilot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.allEpisodesRecyclerView.EpisodeAdapter
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.viewModels.EpisodeViewModel

class ViewAllEpisodesActivity: AppCompatActivity() {

    private lateinit var rvEpisodes: RecyclerView
    private lateinit var episodeViewModel: EpisodeViewModel
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
        val decoration = DividerItemDecoration(this, layoutManager.orientation)
        rvEpisodes.addItemDecoration(decoration)

        episodeViewModel.allEpisodes.observe(this, Observer { allEpisodes ->
            episodes = allEpisodes.toMutableList()
        })
    }
}