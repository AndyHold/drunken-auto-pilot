package com.example.drunkenautopilot.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.repository.EpisodeRepository
import com.example.drunkenautopilot.repository.EpisodeRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EpisodeRepository
    val allEpisodes: LiveData<List<Episode>>
    val activeEpisode: LiveData<Episode?>

    init {
        val episodeDao = EpisodeRoomDatabase.getDatabase(
            application
        ).episodeDao()
        repository =
            EpisodeRepository(episodeDao)
        allEpisodes = repository.allEpisodes
        activeEpisode = repository.activeEpisode
    }

    fun insert(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        episode.id = repository.insert(episode)
    }

    fun update(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(episode)
    }

    fun delete(episodeId: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(episodeId)
    }
}