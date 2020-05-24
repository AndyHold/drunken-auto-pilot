package com.example.drunkenautopilot.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.Episode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EpisodeRepository
    val allEpisodes: LiveData<List<Episode>>

    init {
        val episodeDao = EpisodeRoomDatabase.getDatabase(application).episodeDao()
        repository = EpisodeRepository(episodeDao)
        allEpisodes = repository.allEpisodes
    }

    fun insert(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(episode)
    }

    fun update(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(episode)
    }

    fun delete(episodeId: Long) {
        repository.delete(episodeId)
    }
}